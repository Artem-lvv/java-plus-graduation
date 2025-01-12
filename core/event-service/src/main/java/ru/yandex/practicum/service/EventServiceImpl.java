package ru.yandex.practicum.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import ru.yandex.practicum.event.model.dto.EventDto;
import ru.yandex.practicum.event.model.dto.EventDtoWithObjects;
import ru.yandex.practicum.event.model.dto.UpdateEventDto;
import ru.yandex.practicum.exception.type.ConflictException;
import ru.yandex.practicum.exception.type.NotFoundException;
import ru.yandex.practicum.location.model.dto.CreateLocationDto;
import ru.yandex.practicum.location.model.dto.LocationDto;
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
import java.util.List;
import java.util.Objects;

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

    @Override
    public List<EventDto> getAllByAdmin(final AdminParameter adminParameter) {
        final List<Event> lists = eventStorage.findAll(getSpecification(adminParameter),
                PageRequest.of(adminParameter.getFrom() / adminParameter.getSize(),
                        adminParameter.getSize()));

        lists.forEach(event -> updateStats(event, adminParameter.getRangeStart(), adminParameter.getRangeEnd(), true));

        return lists.stream()
                .map(event -> cs.convert(event, EventDto.class))
                .toList();
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
    public EventDto updateByAdmin(final long eventId, final UpdateEventDto updateEventDto) {
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

        return cs.convert(eventStorage.save(update(eventInStorage, updateEventDto)), EventDto.class);
    }

    @Override
    public EventDtoWithObjects create(final CreateEventDto createEventDto, final long userId) {
        UserDto userDto = adminUserClient.getAll(List.of(userId), 0, 1)
                .stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException(User.class.getSimpleName(), userId));

        User user = cs.convert(userDto, User.class);

        CategoryDto categoryDto = publicCategoryClient.getById(createEventDto.category());

        LocationDto locationDto = getOrCreateLocationDtoByCoordinates(createEventDto.location().lat(),
                createEventDto.location().lon());

        Event event = cs.convert(createEventDto, Event.class);

        event.setInitiator(user.getId());
        event.setCategory(categoryDto.id());
        event.setLocation(locationDto.id());
        event.setCreatedOn(LocalDateTime.now());
        event.setState(State.PENDING);

        eventStorage.save(event);

        return createDtoWithObjects(event, categoryDto, user, locationDto);
    }

    private EventDtoWithObjects createDtoWithObjects(Event event, CategoryDto categoryDto,
                                                     User user, LocationDto locationDto) {
        return EventDtoWithObjects.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(categoryDto)
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(UserWithoutEmailDto.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .build())
                .location(locationDto)
                .paid(event.isPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.isRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(event.getViews())
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
    public List<EventDto> getAllByUserId(final long userId, final int from, final int size) {
        adminUserClient.getAll(List.of(userId), 0, 1)
                .stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException(User.class.getSimpleName(), userId));

        return eventStorage.findAllByInitiator(userId, PageRequest.of(from, size)).stream()
                .map(event -> cs.convert(event, EventDto.class))
                .toList();
    }

    @Override
    public EventDto getByIdAndUserId(final long eventId, final long userId) {
        adminUserClient.getAll(List.of(userId), 0, 1)
                .stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException(User.class.getSimpleName(), userId));

        Event event = eventStorage.getByIdOrElseThrow(eventId);
        checkIfTheUserIsTheEventCreator(userId, eventId);
        return cs.convert(event, EventDto.class);
    }

    @Override
    public EventDto getById(final long eventId, final HttpServletRequest request) {
        Event event = eventStorage.getByIdOrElseThrow(eventId);

        if (event.getState() != State.PUBLISHED) {
            throw new NotFoundException(SIMPLE_NAME, eventId);
        }

        addStats(request);

        updateStats(event, LocalDateTime.now().minusDays(3), LocalDateTime.now().plusDays(3), true);
        return cs.convert(eventStorage.save(event), EventDto.class);
    }

    @Override
    public List<EventDto> getAll(final PublicParameter publicParameter, final HttpServletRequest request) {
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

        final List<Event> lists = eventStorage.findAll(
                predicate, PageRequest.of(publicParameter.getFrom() / publicParameter.getSize(),
                        publicParameter.getSize())
        );

        lists.forEach(event -> updateStats(event, publicParameter.getRangeStart(), publicParameter.getRangeEnd(), false));

        eventStorage.saveAll(lists);

        return lists.stream()
                .map(event -> cs.convert(event, EventDto.class))
                .toList();
    }

    @Override
    public List<EventDto> getAllByLocation(final double lat, final double lon, final double radius) {
        List<Event> events;

        if (radius > 0) {
            events = eventStorage.findAllByLocationAndRadius(lat, lon, radius);
        } else {
            events = eventStorage.findAllEventsByLocation(lat, lon);
        }

        if (ObjectUtils.isEmpty(events)) {
            return List.of();
        }

        return events.stream()
                .map(event -> cs.convert(event, EventDto.class))
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

        UserDto userDto = adminUserClient.getAll(List.of(userId), 0, 1)
                .stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException(User.class.getSimpleName(), userId));

        User user = cs.convert(userDto, User.class);

        CategoryDto categoryDto = publicCategoryClient.getById(eventInStorage.getCategory());
        LocationDto locationDto = adminLocationClient.getById(eventInStorage.getLocation());

        return createDtoWithObjects(eventInStorage, categoryDto, user, locationDto);
    }

    private void addStats(final HttpServletRequest request) {
        EndpointHit endpointHit = new EndpointHit();
        endpointHit.app("event-service");
        endpointHit.ip(request.getRemoteAddr());
        endpointHit.uri(request.getRequestURI());
        endpointHit.timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).toString());

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

        event.setViews(views);
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
