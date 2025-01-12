package ru.yandex.practicum.service;

import org.springframework.util.ObjectUtils;
import ru.yandex.practicum.AdminEventClient;
import ru.yandex.practicum.AdminUserClient;
import ru.yandex.practicum.event.model.AdminParameter;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.event.model.dto.EventDto;
import ru.yandex.practicum.event.model.dto.UpdateEventDto;
import ru.yandex.practicum.exception.type.ConflictException;
import ru.yandex.practicum.exception.type.NotFoundException;
import ru.yandex.practicum.request.model.Request;
import ru.yandex.practicum.request.model.dto.RequestDto;
import ru.yandex.practicum.request.model.dto.RequestStatusUpdateResultDto;
import ru.yandex.practicum.request.model.dto.UpdateRequestByIdsDto;
import ru.yandex.practicum.state.State;
import ru.yandex.practicum.storage.RequestStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.user.model.User;
import ru.yandex.practicum.user.model.dto.UserDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private static final String SIMPLE_NAME = Request.class.getSimpleName();
    @Qualifier("mvcConversionService")
    private final ConversionService cs;
    private final RequestStorage requestStorage;
//    private final UserStorage userStorage;
    private final AdminUserClient adminUserClient;
//    private final EventStorage eventStorage;
    private final AdminEventClient adminEventClient;

    @Override
    public RequestDto create(final long userId, final long eventId) {
        requestStorage.ifExistsByRequesterIdAndEventIdThenThrow(userId, eventId);
//        User user = userStorage.getByIdOrElseThrow(userId);
        UserDto userDto = adminUserClient.getAll(List.of(userId), 0, 1)
                .stream()
                .findFirst().orElseThrow(() -> new NotFoundException(User.class.getSimpleName(), userId));

        User user = cs.convert(userDto, User.class);

//        Event event = eventStorage.getByIdOrElseThrow(eventId);
//        EventDto eventDto = adminEventClient.getAll(AdminParameter.builder()
//                        .events(List.of(eventId))
//                        .build())
        EventDto eventDto = adminEventClient.getAll(null,
                        null,
                        null,
                        List.of(eventId),
                        null,
                        null,
                        0
                        ,1)
                .stream()
                .findFirst().orElseThrow(() -> new NotFoundException(Event.class.getSimpleName(), eventId));

        Event event = cs.convert(eventDto, Event.class);

        if (event.getInitiator().getId() == user.getId()) {
            throw new ConflictException("%s : can`t add a request to your own: %d eventId: %d".formatted(SIMPLE_NAME,
                    userId, eventId));
        }

        if (event.getState() != State.PUBLISHED) {
            throw new ConflictException("Cannot add a request to an unpublished eventId: %d".formatted(eventId));
        } else if (event.getParticipantLimit() != 0
                && requestStorage.countByEventIdAndStatus(event.getId(), State.CONFIRMED) >= event.getParticipantLimit()) {
            throw new ConflictException("Event participation limit exceeded eventId: %d".formatted(eventId));
        }

        Request request = Request.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(user)
                .status(event.getParticipantLimit() == 0 || !event.isRequestModeration() ? State.CONFIRMED
                        : State.PENDING)
                .build();

        if (request.getStatus() == State.CONFIRMED) {
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
//            eventStorage.save(event);
            UpdateEventDto updateEventDto = cs.convert(event, UpdateEventDto.class);
            adminEventClient.update(updateEventDto, event.getId());
        }

        return cs.convert(requestStorage.save(request), RequestDto.class);
    }

    @Override
    public List<RequestDto> getAll(final long userId) {
        return requestStorage.findAllByRequesterId(userId).stream()
                .map(request -> cs.convert(request, RequestDto.class))
                .toList();
    }

    @Override
    public RequestDto cancel(final long userId, final long requestId) {
//        userStorage.existsByIdOrElseThrow(userId);
        adminUserClient.getAll(List.of(userId), 0, 1)
                .stream()
                .findFirst().orElseThrow(() -> new NotFoundException(User.class.getSimpleName(), userId));

        Request request = requestStorage.getByIdOrElseThrow(requestId);

        if (request.getStatus() == State.CONFIRMED) {
            Event event = request.getEvent();
            event.setConfirmedRequests(event.getConfirmedRequests() - 1);
//            eventStorage.save(event);
            adminEventClient.update(cs.convert(event, UpdateEventDto.class), event.getId());
        }

        request.setStatus(State.CANCELED);

        return cs.convert(requestStorage.save(request), RequestDto.class);
    }

    @Override
    public List<RequestDto> getRequestsByUserIdAndEventId(long userId, long eventId) {
        return requestStorage.findAllByRequesterIdAndEventId(userId, eventId)
                .stream()
                .map(request -> cs.convert(request, RequestDto.class))
                .toList();
    }

    @Override
    public RequestStatusUpdateResultDto updateRequestsStatusByUserIdAndEventId(long userId, long eventId,
                                                                               UpdateRequestByIdsDto updateRequestByIdsDto) {

        List<Request> requests = requestStorage.findAllByIdInAndEventId(updateRequestByIdsDto.requestIds(), eventId);

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
                request.setStatus(updateRequestByIdsDto.status());

                if (countRequest++ == event.getParticipantLimit()) {
                    request.setStatus(State.CANCELED);
                }

                requestsForSave.add(request);

                if (updateRequestByIdsDto.status() == State.CONFIRMED) {
                    result.confirmedRequests().add(cs.convert(request, RequestDto.class));

                    // доб
//                    event.setParticipantLimit(event.getConfirmedRequests() + 1);
//                    adminEventClient.update(cs.convert(event, UpdateEventDto.class), event.getId());
                }

                if (updateRequestByIdsDto.status() == State.REJECTED) {
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
    public List<RequestDto> getRequestsByEventId(long eventId) {
        return requestStorage.findAllByEventId(eventId)
                .stream()
                .map(request -> cs.convert(request, RequestDto.class))
                .toList();
    }
}
