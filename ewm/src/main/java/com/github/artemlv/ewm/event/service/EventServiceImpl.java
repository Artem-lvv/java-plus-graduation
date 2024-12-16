package com.github.artemlv.ewm.event.service;

import com.github.artemlv.ewm.category.model.Category;
import com.github.artemlv.ewm.category.storage.CategoryStorage;
import com.github.artemlv.ewm.event.model.AdminParameter;
import com.github.artemlv.ewm.event.model.Event;
import com.github.artemlv.ewm.event.model.PublicParameter;
import com.github.artemlv.ewm.event.model.dto.CreateEventDto;
import com.github.artemlv.ewm.event.model.dto.EventDto;
import com.github.artemlv.ewm.event.model.dto.UpdateEventDto;
import com.github.artemlv.ewm.event.storage.EventStorage;
import com.github.artemlv.ewm.exception.type.ConflictException;
import com.github.artemlv.ewm.exception.type.NotFoundException;
import com.github.artemlv.ewm.location.model.Location;
import com.github.artemlv.ewm.location.service.LocationService;
import com.github.artemlv.ewm.request.model.Request;
import com.github.artemlv.ewm.request.model.dto.RequestDto;
import com.github.artemlv.ewm.request.model.dto.RequestStatusUpdateResultDto;
import com.github.artemlv.ewm.request.model.dto.UpdateRequestByIdsDto;
import com.github.artemlv.ewm.request.storage.RequestStorage;
import com.github.artemlv.ewm.state.State;
import com.github.artemlv.ewm.user.model.User;
import com.github.artemlv.ewm.user.storage.UserStorage;
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
import ru.yandex.practicum.SearchStats;
import ru.yandex.practicum.client.StatsClient;
import ru.yandex.practicum.dto.CreateStatsDto;
import ru.yandex.practicum.dto.StatCountHitsDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.github.artemlv.ewm.event.model.QEvent.event;


@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private static final String SIMPLE_NAME = Event.class.getSimpleName();
    @Qualifier("mvcConversionService")
    private final ConversionService cs;
    private final EventStorage eventStorage;
    private final UserStorage userStorage;
    private final CategoryStorage categoryStorage;
    private final LocationService locationService;
    private final RequestStorage requestStorage;
    private final StatsClient statsClient;

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
            eventInStorage.setCategory(categoryStorage.getByIdOrElseThrow(updateEventDto.category()));
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
            eventInStorage.setLocation(cs.convert(
                    locationService.getByCoordinatesOrElseCreate(updateEventDto.location()), Location.class)
            );
        }

        return cs.convert(eventStorage.save(update(eventInStorage, updateEventDto)), EventDto.class);
    }

    @Override
    public EventDto create(final CreateEventDto createEventDto, final long userId) {
        final User user = userStorage.getByIdOrElseThrow(userId);
        final Category category = categoryStorage.getByIdOrElseThrow(createEventDto.category());
        final Location location = cs.convert(locationService.getByCoordinatesOrElseCreate(createEventDto.location()),
                Location.class);

        Event event = cs.convert(createEventDto, Event.class);

        event.setInitiator(user);
        event.setCategory(category);
        event.setLocation(location);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(State.PENDING);

        return cs.convert(eventStorage.save(event), EventDto.class);
    }

    @Override
    public List<EventDto> getAllByUserId(final long userId, final int from, final int size) {
        userStorage.existsByIdOrElseThrow(userId);
        return eventStorage.findAllByInitiatorId(userId, PageRequest.of(from, size)).stream()
                .map(event -> cs.convert(event, EventDto.class))
                .toList();
    }

    @Override
    public List<RequestDto> getRequestsByUserIdAndEventId(final long userId, final long eventId) {
        userStorage.existsByIdOrElseThrow(userId);
        eventStorage.existsByIdOrElseThrow(eventId);
        return requestStorage.findAllByEventId(eventId).stream()
                .map(event -> cs.convert(event, RequestDto.class))
                .toList();
    }

    @Override
    public EventDto getByIdAndUserId(final long eventId, final long userId) {
        userStorage.existsByIdOrElseThrow(userId);
        Event event = eventStorage.getByIdOrElseThrow(eventId);
        checkIfTheUserIsTheEventCreator(userId, eventId);
        return cs.convert(event, EventDto.class);
    }

    @Override
    public RequestStatusUpdateResultDto updateRequestsStatusByUserIdAndEventId(final long userId, final long eventId,
                                                                               UpdateRequestByIdsDto update) {
        List<Request> requests = requestStorage.findAllByIdInAndEventId(update.requestIds(), eventId);

        if (ObjectUtils.isEmpty(requests)) {
            throw new NotFoundException("No requests found for event id " + eventId);
        }

        int countRequest = requestStorage.countByEventIdAndStatus(eventId, State.CONFIRMED);

        RequestStatusUpdateResultDto result = RequestStatusUpdateResultDto.builder()
                .confirmedRequests(new ArrayList<>())
                .rejectedRequests(new ArrayList<>())
                .build();

        List<Request> requestsForSave = new ArrayList<>();

        for (Request request : requests) {
            if (request.getStatus() != State.PENDING) {
                throw new ConflictException(
                        "The status can only be changed for applications that are in a pending state"
                );
            }

            Event event = request.getEvent();

            if (countRequest >= event.getParticipantLimit()) {
                throw new ConflictException("The limit on applications for this event has been reached");
            }

            if (event.getParticipantLimit() != 0 && event.isRequestModeration()) {
                request.setStatus(update.status());

                if (countRequest++ == event.getParticipantLimit()) {
                    request.setStatus(State.CANCELED);
                }

                requestsForSave.add(request);

                if (update.status() == State.CONFIRMED) {
                    result.confirmedRequests().add(cs.convert(request, RequestDto.class));
                }

                if (update.status() == State.REJECTED) {
                    result.rejectedRequests().add(cs.convert(request, RequestDto.class));
                }
            }
        }

        if (!requestsForSave.isEmpty()) {
            requestStorage.saveAll(requestsForSave);
        }

        return result;
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
            predicate = predicate.and(event.category.id.in(publicParameter.getCategories()));
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
    public EventDto updateByUser(final long userId, final long eventId, final UpdateEventDto updateEventDto) {
        Event eventInStorage = eventStorage.getByIdOrElseThrow(eventId);

        checkEventIsPublished(eventInStorage.getState());

        if (eventInStorage.getInitiator().getId() != userId) {
            throw new ConflictException("The initiator does not belong to this event");
        }

        if (!ObjectUtils.isEmpty(updateEventDto.stateAction())) {
            switch (updateEventDto.stateAction()) {
                case CANCEL_REVIEW -> eventInStorage.setState(State.CANCELED);

                case SEND_TO_REVIEW -> eventInStorage.setState(State.PENDING);
            }
        }

        return cs.convert(eventStorage.save(update(eventInStorage, updateEventDto)), EventDto.class);
    }

    private void addStats(final HttpServletRequest request) {
        statsClient.save(CreateStatsDto.builder()
                .app("ewm")
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build()
        );
    }

    private void updateStats(Event event, final LocalDateTime startRange, final LocalDateTime endRange,
                             final boolean unique) {

        List<StatCountHitsDto> stats = statsClient.get(SearchStats.builder()
                .start(startRange)
                .end(endRange)
                .uris(List.of("/events/" + event.getId()))
                .unique(unique)
                .build()
        );
        long views = 0L;

        for (StatCountHitsDto stat : stats) {
            views += stat.hits();
        }

        int confirmedRequests = requestStorage.countByEventIdAndStatus(event.getId(), State.CONFIRMED);

        event.setViews(views);
        event.setConfirmedRequests(confirmedRequests);
    }

    private void checkIfTheUserIsTheEventCreator(final long userId, final long eventId) {
        if (userId == eventId) {
            throw new ConflictException(String.format("Event originator userId: %d cannot add a membership request " +
                    "in its event eventId: %d", userId, eventId));
        }
    }

    private Specification<Event> checkCategories(final List<Long> categories) {
        return ObjectUtils.isEmpty(categories) ? null
                : ((root, query, criteriaBuilder) -> root.get("category").get("id").in(categories));
    }

    private Specification<Event> checkByUserIds(final List<Long> userIds) {
        return ObjectUtils.isEmpty(userIds) ? null
                : ((root, query, criteriaBuilder) -> root.get("initiator").get("id").in(userIds));
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
                .and(checkRangeStart(adminParameter.getRangeStart()))
                .and(checkRangeEnd(adminParameter.getRangeEnd()));
    }

    private BooleanExpression getPredicate(final AdminParameter adminParameter) {
        BooleanExpression predicate = event.isNotNull();

        if (!ObjectUtils.isEmpty(adminParameter.getUsers())) {
            predicate = predicate.and(event.initiator.id.in(adminParameter.getUsers()));
        }

        if (!ObjectUtils.isEmpty(adminParameter.getStates())) {
            predicate = predicate.and(event.state.in(adminParameter.getStates()));
        }

        if (!ObjectUtils.isEmpty(adminParameter.getCategories())) {
            predicate = predicate.and(event.category.id.in(adminParameter.getCategories()));
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
