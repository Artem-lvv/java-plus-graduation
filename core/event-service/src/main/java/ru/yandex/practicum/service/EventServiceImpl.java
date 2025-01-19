package ru.yandex.practicum.service;

import com.google.protobuf.Timestamp;
import com.querydsl.core.types.dsl.BooleanExpression;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import ru.yandex.practicum.AdminLocationClient;
import ru.yandex.practicum.AdminUserClient;
import ru.yandex.practicum.PrivateUserRequestClient;
import ru.yandex.practicum.PublicCategoryClient;
import ru.yandex.practicum.category.model.dto.CategoryDto;
import ru.yandex.practicum.event.model.AdminParameter;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.event.model.PublicParameter;
import ru.yandex.practicum.event.model.dto.CreateEventDto;
import ru.yandex.practicum.event.model.dto.EventDtoWithObjects;
import ru.yandex.practicum.event.model.dto.UpdateEventDto;
import ru.yandex.practicum.exception.type.BadRequestException;
import ru.yandex.practicum.exception.type.ConflictException;
import ru.yandex.practicum.exception.type.NotFoundException;
import ru.yandex.practicum.grpc.collector.controller.UserActionControllerGrpc;
import ru.yandex.practicum.grpc.collector.user.ActionTypeProto;
import ru.yandex.practicum.grpc.collector.user.UserActionProto;
import ru.yandex.practicum.grpc.recommendation.RecommendationsControllerGrpc;
import ru.yandex.practicum.grpc.recommendation.RecommendedEventProto;
import ru.yandex.practicum.grpc.recommendation.UserPredictionsRequestProto;
import ru.yandex.practicum.location.model.dto.CreateLocationDto;
import ru.yandex.practicum.location.model.dto.LocationDto;
import ru.yandex.practicum.request.model.dto.RequestDto;
import ru.yandex.practicum.state.State;
import ru.yandex.practicum.stats.api.StatsServiceApi;
import ru.yandex.practicum.stats.model.EndpointHit;
import ru.yandex.practicum.stats.model.ViewStats;
import ru.yandex.practicum.storage.EventStorage;
import ru.yandex.practicum.user.model.User;
import ru.yandex.practicum.user.model.dto.UserDto;
import ru.yandex.practicum.user.model.dto.UserWithoutEmailDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static ru.yandex.practicum.event.model.QEvent.event;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private static final String SIMPLE_NAME = Event.class.getSimpleName();
    @Qualifier("mvcConversionService")
    private final ConversionService cs;
    private final StatsServiceApi statsService;
    private final EventStorage eventStorage;
    private final AdminUserClient adminUserClient;
    private final PublicCategoryClient publicCategoryClient;
    private final PrivateUserRequestClient privateUserRequestClient;
    private final AdminLocationClient adminLocationClient;
    private final int MAX_RESULTS_RECOMMENDATION_SIZE = 10;
    @GrpcClient("analyzer")
    private RecommendationsControllerGrpc.RecommendationsControllerBlockingStub clientAnalyzerGrpc;

    @GrpcClient("collector")
    private UserActionControllerGrpc.UserActionControllerBlockingStub clientCollectorGrpc;


    @Override
    public List<EventDtoWithObjects> getAllByAdmin(final AdminParameter adminParameter) {
        final List<Event> events = eventStorage.findAll(getSpecification(adminParameter),
                PageRequest.of(adminParameter.getFrom() / adminParameter.getSize(),
                        adminParameter.getSize()));

        Map<Long, UserDto> userDtoMap = getLongUserDtoMap(events);
        Map<Long, CategoryDto> categoryDtoMap = getLongCategoryDtoMap();
        Map<Long, LocationDto> locationDtoMap = getLongLocationDtoMap(events);


        List<EventDtoWithObjects> eventDtoWithObjects = new ArrayList<>();
        events.forEach(event -> eventDtoWithObjects.add(createDtoWithObjects(event,
                categoryDtoMap.get(event.getCategory()),
                userDtoMap.get(event.getInitiator()),
                locationDtoMap.get(event.getLocation()))));

        return eventDtoWithObjects;
    }

    private Map<Long, LocationDto> getLongLocationDtoMap(List<Event> events) {
        return getLocationDtoMapByEvents(adminLocationClient.getAllByIds(events
                .stream()
                .map(Event::getLocation)
                .toList()));
    }

    private Map<Long, CategoryDto> getLongCategoryDtoMap() {
        return publicCategoryClient.getAll(0, Integer.MAX_VALUE)
                .stream()
                .collect(Collectors.toMap(CategoryDto::id, categoryDto1 -> categoryDto1));
    }

    private Map<Long, UserDto> getLongUserDtoMap(List<Event> events) {
        return adminUserClient.getAll(events
                                .stream()
                                .map(Event::getInitiator)
                                .toList(),
                        0,
                        Integer.MAX_VALUE)
                .stream()
                .collect(Collectors.toMap(UserDto::id, userDto -> userDto));
    }

    private Event update(Event eventInStorage, final UpdateEventDto updateEventDto) {

        if (!ObjectUtils.isEmpty(updateEventDto.participantLimit())) {
            eventInStorage.setParticipantLimit(updateEventDto.participantLimit());
        }

        if (!ObjectUtils.isEmpty(updateEventDto.paid())) {
            eventInStorage.setPaid(updateEventDto.paid());
        }

        if (!ObjectUtils.isEmpty(updateEventDto.annotation())) {
            eventInStorage.setAnnotation(updateEventDto.annotation());
        }

        if (!ObjectUtils.isEmpty(updateEventDto.description())) {
            eventInStorage.setDescription(updateEventDto.description());
        }

        if (!ObjectUtils.isEmpty(updateEventDto.title())) {
            eventInStorage.setTitle(updateEventDto.title());
        }

        if (updateEventDto.category() != 0) {
            CategoryDto categoryDto = publicCategoryClient.getById(updateEventDto.category());
            eventInStorage.setCategory(categoryDto.id());
        }

        return eventInStorage;
    }

    @Override
    public EventDtoWithObjects updateByAdmin(final long eventId, final UpdateEventDto updateEventDto) {
        Event eventInStorage = eventStorage.getByIdOrElseThrow(eventId);

        if (!ObjectUtils.isEmpty(updateEventDto.stateAction())) {
            switch (updateEventDto.stateAction()) {
                case REJECT_EVENT -> {
                    checkEventIsPublished(eventInStorage.getState());
                    eventInStorage.setState(State.CANCELED);
                }

                case PUBLISH_EVENT -> {
                    if (eventInStorage.getState() != State.PENDING) {
                        throw new ConflictException("An event can only be published if it is in a pending publication state");
                    }
                    if (LocalDateTime.now().plusHours(1).isAfter(eventInStorage.getEventDate())) {
                        throw new ConflictException("The start date of the event being modified must be no earlier "
                                + "than an hour before from date of publication");
                    }

                    eventInStorage.setState(State.PUBLISHED);
                    eventInStorage.setPublishedOn(LocalDateTime.now());
                }
            }
        }

        if (!ObjectUtils.isEmpty(updateEventDto.location())) {
            LocationDto locationDto = getOrCreateLocationDtoByCoordinates(updateEventDto.location().lat(),
                    updateEventDto.location().lon());
            eventInStorage.setLocation(locationDto.id());
        }

        eventStorage.save(update(eventInStorage, updateEventDto));

        UserDto userDto = getUserDto(eventInStorage.getInitiator());

        CategoryDto categoryDto = publicCategoryClient.getById(eventInStorage.getCategory());
        LocationDto locationDto = adminLocationClient.getById(eventInStorage.getLocation());

        return createDtoWithObjects(eventInStorage, categoryDto, userDto, locationDto);
    }

    @Override
    public EventDtoWithObjects create(final CreateEventDto createEventDto, final long userId) {
        UserDto userDto = getUserDto(userId);

        CategoryDto categoryDto = publicCategoryClient.getById(createEventDto.category());

        LocationDto locationDto = getOrCreateLocationDtoByCoordinates(createEventDto.location().lat(),
                createEventDto.location().lon());

        Event event = cs.convert(createEventDto, Event.class);

        event.setInitiator(userDto.id());
        event.setCategory(categoryDto.id());
        event.setLocation(locationDto.id());
        event.setCreatedOn(LocalDateTime.now());
        event.setState(State.PENDING);

        eventStorage.save(event);

        return createDtoWithObjects(event, categoryDto, userDto, locationDto);
    }

    private UserDto getUserDto(long userId) {
        return adminUserClient.getAll(List.of(userId), 0, 1)
                .stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException(User.class.getSimpleName(), userId));
    }

    private EventDtoWithObjects createDtoWithObjects(Event event, CategoryDto categoryDto,
                                                     UserDto userDto, LocationDto locationDto) {
        return EventDtoWithObjects.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(categoryDto)
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(UserWithoutEmailDto.builder()
                        .id(userDto.id())
                        .name(userDto.name())
                        .build())
                .location(locationDto)
                .paid(event.isPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.isRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .rating(event.getRating())
                .build();
    }

    private LocationDto getOrCreateLocationDtoByCoordinates(double lat, double lon) {
        LocationDto locationDto = adminLocationClient.getByCoordinates(lat, lon);

        if (Objects.isNull(locationDto)) {
            locationDto = adminLocationClient.create(CreateLocationDto.builder()
                    .lat(lat)
                    .lon(lon)
                    .name("lat " + lat + " lon " + lon)
                    .radius(0.0)
                    .build());
        }
        return locationDto;
    }

    @Override
    public List<EventDtoWithObjects> getAllByUserId(final long userId, final int from, final int size) {
        UserDto userDto = getUserDto(userId);

        List<Event> events = eventStorage.findAllByInitiator(userId, PageRequest.of(from, size));
        Map<Long, CategoryDto> categoryDtoMap = getLongCategoryDtoMap();

        Map<Long, LocationDto> locationDtoMap = getLocationDtoMapByEvents(adminLocationClient.getAllByIds(events
                .stream()
                .map(Event::getLocation)
                .toList()));

        return events
                .stream()
                .map(event1 -> createDtoWithObjects(event1,
                        categoryDtoMap.get(event1.getCategory()),
                        userDto,
                        locationDtoMap.get(event1.getLocation())))
                .toList();

    }

    private Map<Long, LocationDto> getLocationDtoMapByEvents(List<LocationDto> adminLocationClient) {
        return adminLocationClient
                .stream()
                .collect(Collectors.toMap(LocationDto::id, locationDto -> locationDto));
    }

    @Override
    public EventDtoWithObjects getByIdAndUserId(final long eventId, final long userId) {
        UserDto userDto = getUserDto(userId);

        Event event = eventStorage.getByIdOrElseThrow(eventId);
        checkIfTheUserIsTheEventCreator(userId, eventId);

        CategoryDto categoryDto = publicCategoryClient.getById(event.getCategory());
        LocationDto locationDto = adminLocationClient.getById(event.getLocation());

        return createDtoWithObjects(event, categoryDto, userDto, locationDto);
    }

    @Override
    public EventDtoWithObjects getById(final long eventId, final HttpServletRequest request, long userId) {
        Event event = eventStorage.getByIdOrElseThrow(eventId);

        if (event.getState() != State.PUBLISHED) {
            throw new NotFoundException(SIMPLE_NAME, eventId);
        }

        addStats(request);

        UserActionProto userActionProto = UserActionProto.newBuilder()
                .setEventId(eventId)
                .setUserId(userId)
                .setActionType(ActionTypeProto.ACTION_VIEW)
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(System.currentTimeMillis() / 1000)
                        .build())
                .build();

        clientCollectorGrpc.collectUserAction(userActionProto);

        UserDto userDto = getUserDto(event.getInitiator());
        CategoryDto categoryDto = publicCategoryClient.getById(event.getCategory());
        LocationDto locationDto = adminLocationClient.getById(event.getLocation());

        return createDtoWithObjects(event, categoryDto, userDto, locationDto);
    }

    @Override
    public List<EventDtoWithObjects> getAll(final PublicParameter publicParameter, final HttpServletRequest request) {
        BooleanExpression predicate = event.isNotNull();

        if (!ObjectUtils.isEmpty(publicParameter.getText())) {
            predicate = predicate.and(event.annotation.likeIgnoreCase(publicParameter.getText()));
        }

        if (!ObjectUtils.isEmpty(publicParameter.getCategories())) {
            predicate = predicate.and(event.category.in(publicParameter.getCategories()));
        }
        if (!ObjectUtils.isEmpty(publicParameter.getPaid())) {
            predicate = predicate.and(event.paid.eq(publicParameter.getPaid()));
        }

        predicate = predicate.and(event.createdOn.between(publicParameter.getRangeStart(), publicParameter.getRangeEnd()));

        addStats(request);

        final List<Event> events = eventStorage.findAll(
                predicate, PageRequest.of(publicParameter.getFrom() / publicParameter.getSize(),
                        publicParameter.getSize())
        );

        eventStorage.saveAll(events);

        Map<Long, UserDto> userDtoMap = getLongUserDtoMap(events);

        Map<Long, CategoryDto> categoryDtoMap = getLongCategoryDtoMap();

        Map<Long, LocationDto> locationDtoMap = getLocationDtoMapByEvents(adminLocationClient.getAllByIds(events
                .stream()
                .map(Event::getLocation)
                .toList()));

        return events
                .stream()
                .map(event1 -> createDtoWithObjects(event1,
                        categoryDtoMap.get(event1.getCategory()),
                        userDtoMap.get(event1.getInitiator()),
                        locationDtoMap.get(event1.getLocation())))
                .toList();

    }

    @Override
    public List<EventDtoWithObjects> getAllByLocation(final double lat, final double lon, final double radius) {
        Map<Long, LocationDto> locationDtoMap = getLocationDtoMapByEvents(adminLocationClient
                .getAllByCoordinates(lat, lon, radius));

        List<Event> events = eventStorage.findAllByLocationIn(locationDtoMap.keySet());
        Map<Long, UserDto> userDtoMap = getLongUserDtoMap(events);
        Map<Long, CategoryDto> categoryDtoMap = getLongCategoryDtoMap();

        return events
                .stream()
                .map(event1 -> createDtoWithObjects(event1,
                        categoryDtoMap.get(event1.getCategory()),
                        userDtoMap.get(event1.getInitiator()),
                        locationDtoMap.get(event1.getLocation())))
                .toList();

    }

    @Override
    public EventDtoWithObjects updateByUser(final long userId, final long eventId, final UpdateEventDto updateEventDto) {
        Event eventInStorage = eventStorage.getByIdOrElseThrow(eventId);

        checkEventIsPublished(eventInStorage.getState());

        if (eventInStorage.getInitiator() != userId) {
            throw new ConflictException("The initiator does not belong to this event");
        }

        if (!ObjectUtils.isEmpty(updateEventDto.stateAction())) {
            switch (updateEventDto.stateAction()) {
                case CANCEL_REVIEW -> eventInStorage.setState(State.CANCELED);

                case SEND_TO_REVIEW -> eventInStorage.setState(State.PENDING);
            }
        }

        eventStorage.save(update(eventInStorage, updateEventDto));

        UserDto userDto = getUserDto(userId);

        CategoryDto categoryDto = publicCategoryClient.getById(eventInStorage.getCategory());
        LocationDto locationDto = adminLocationClient.getById(eventInStorage.getLocation());

        return createDtoWithObjects(eventInStorage, categoryDto, userDto, locationDto);
    }

    @Override
    public List<EventDtoWithObjects> getRecommendations(long userId) {
        UserDto userDto = getUserDto(userId);

        UserPredictionsRequestProto predictionsRequestProto = UserPredictionsRequestProto.newBuilder()
                .setUserId((int) userId)
                .setMaxResults(MAX_RESULTS_RECOMMENDATION_SIZE)
                .build();
        Iterator<RecommendedEventProto> recommendationsForUser = clientAnalyzerGrpc
                .getRecommendationsForUser(predictionsRequestProto);

        Map<Integer, Float> eventIdToScopeMap = asStream(recommendationsForUser)
                .collect(Collectors.toMap(recommendedEventProto -> recommendedEventProto.getEventId(),
                        recommendedEventProto -> recommendedEventProto.getScore()));

        List<Event> events = eventStorage.findAllById(eventIdToScopeMap.keySet()
                .stream()
                .map(long.class::cast)
                .collect(Collectors.toSet()));

        events.forEach(event -> event.setRating(eventIdToScopeMap.get(event.getId())));

        Map<Long, CategoryDto> categoryDtoMap = getLongCategoryDtoMap();

        Map<Long, LocationDto> locationDtoMap = getLocationDtoMapByEvents(adminLocationClient.getAllByIds(events
                .stream()
                .map(Event::getLocation)
                .toList()));

        return events
                .stream()
                .map(event1 -> createDtoWithObjects(event1,
                        categoryDtoMap.get(event1.getCategory()),
                        userDto,
                        locationDtoMap.get(event1.getLocation())))
                .toList();
    }

    @Override
    public void addLikeEvent(long eventId, long userId) {
        UserDto userDto = getUserDto(userId);

        List<RequestDto> requestsByUserIdAndEventId = privateUserRequestClient
                .getAllRequests(userId)
                .stream()
                .filter(requestDto -> requestDto.event() == eventId
                        && requestDto.status() == State.CONFIRMED)
                .toList();

        if (requestsByUserIdAndEventId.isEmpty()) {
            throw new BadRequestException("Completed event not found");
        }

        UserActionProto userActionProto = UserActionProto.newBuilder()
                .setEventId(eventId)
                .setUserId(userId)
                .setActionType(ActionTypeProto.ACTION_LIKE)
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(System.currentTimeMillis() / 1000)
                        .build())
                .build();

        clientCollectorGrpc.collectUserAction(userActionProto);
    }

    private Stream<RecommendedEventProto> asStream(Iterator<RecommendedEventProto> iterator) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED),
                false
        );
    }

    private void addStats(final HttpServletRequest request) {
        EndpointHit endpointHit = new EndpointHit();
        endpointHit.app("event-service");
        endpointHit.ip(request.getRemoteAddr());
        endpointHit.uri(request.getRequestURI());
        endpointHit.timestamp(LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).toString());

        statsService.hit(endpointHit);
    }

    private void updateStats(Event event, final LocalDateTime startRange, final LocalDateTime endRange,
                             final boolean unique) {

        List<ViewStats> stats = statsService.getStats(startRange.toString(),
                endRange.toString(),
                List.of("/events/" + event.getId()),
                unique).getBody();

        long views = 0L;

        for (ViewStats stat : stats) {
            views += stat.getHits();
        }

        long confirmedRequests = privateUserRequestClient.getRequestsByEventId(event.getId())
                .stream()
                .filter(requestDto -> requestDto.status() == State.CONFIRMED)
                .count();

        event.setRating(views);
        event.setConfirmedRequests((int) confirmedRequests);
    }

    private void checkIfTheUserIsTheEventCreator(final long userId, final long eventId) {
        if (userId == eventId) {
            throw new ConflictException(String.format("Event originator userId: %d cannot add a membership request " +
                    "in its event eventId: %d", userId, eventId));
        }
    }

    private Specification<Event> checkCategories(final List<Long> categories) {
        return ObjectUtils.isEmpty(categories) ? null
                : ((root, query, criteriaBuilder) -> root.get("category").in(categories));
    }

    private Specification<Event> checkEvents(final List<Long> eventsId) {
        return ObjectUtils.isEmpty(eventsId) ? null
                : ((root, query, criteriaBuilder) -> root.get("id").in(eventsId));
    }

    private Specification<Event> checkByUserIds(final List<Long> userIds) {
        return ObjectUtils.isEmpty(userIds) ? null
                : ((root, query, criteriaBuilder) -> root.get("initiator").in(userIds));
    }

    private Specification<Event> checkStates(final List<State> states) {
        return ObjectUtils.isEmpty(states) ? null
                : ((root, query, criteriaBuilder) -> root.get("state").as(String.class).in(states.stream()
                .map(Enum::toString)
                .toList())
        );
    }

    private Specification<Event> checkRangeStart(final LocalDateTime start) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"),
                        start == null ? LocalDateTime.now() : start));
    }

    private Specification<Event> checkRangeEnd(final LocalDateTime end) {
        return ObjectUtils.isEmpty(end) ? null
                : ((root, query, criteriaBuilder) -> criteriaBuilder.lessThan(root.get("eventDate"), end));
    }

    private Specification<Event> getSpecification(final AdminParameter adminParameter) {
        return Specification.where(checkByUserIds(adminParameter.getUsers()))
                .and(checkStates(adminParameter.getStates()))
                .and(checkCategories(adminParameter.getCategories()))
                .and(checkEvents(adminParameter.getEvents()))
                .and(checkRangeStart(adminParameter.getRangeStart()))
                .and(checkRangeEnd(adminParameter.getRangeEnd()));
    }

    private BooleanExpression getPredicate(final AdminParameter adminParameter) {
        BooleanExpression predicate = event.isNotNull();

        if (!ObjectUtils.isEmpty(adminParameter.getUsers())) {
            predicate = predicate.and(event.initiator.in(adminParameter.getUsers()));
        }

        if (!ObjectUtils.isEmpty(adminParameter.getStates())) {
            predicate = predicate.and(event.state.in(adminParameter.getStates()));
        }

        if (!ObjectUtils.isEmpty(adminParameter.getCategories())) {
            predicate = predicate.and(event.category.in(adminParameter.getCategories()));
        }

        predicate = predicate.and(event.createdOn.between(adminParameter.getRangeStart(), adminParameter.getRangeEnd()));

        return predicate;
    }

    private void checkEventIsPublished(final State state) {
        if (state == State.PUBLISHED) {
            throw new ConflictException("An event can only be rejected if it has not yet been published");
        }
    }
}
