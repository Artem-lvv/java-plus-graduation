package ru.yandex.practicum.service;

import com.google.protobuf.Timestamp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import ru.yandex.practicum.AdminEventClient;
import ru.yandex.practicum.AdminUserClient;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.event.model.dto.EventDtoWithObjects;
import ru.yandex.practicum.event.model.dto.UpdateEventDto;
import ru.yandex.practicum.exception.type.ConflictException;
import ru.yandex.practicum.exception.type.NotFoundException;
import ru.yandex.practicum.grpc.collector.controller.UserActionControllerGrpc;
import ru.yandex.practicum.grpc.collector.user.ActionTypeProto;
import ru.yandex.practicum.grpc.collector.user.UserActionProto;
import ru.yandex.practicum.request.model.Request;
import ru.yandex.practicum.request.model.dto.RequestDto;
import ru.yandex.practicum.request.model.dto.RequestStatusUpdateResultDto;
import ru.yandex.practicum.request.model.dto.UpdateRequestByIdsDto;
import ru.yandex.practicum.state.State;
import ru.yandex.practicum.storage.RequestStorage;
import ru.yandex.practicum.user.model.User;
import ru.yandex.practicum.user.model.dto.UserDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private static final String SIMPLE_NAME = Request.class.getSimpleName();
    @Qualifier("mvcConversionService")
    private final ConversionService cs;
    private final RequestStorage requestStorage;
    private final AdminUserClient adminUserClient;
    private final AdminEventClient adminEventClient;

    @GrpcClient("collector")
    private UserActionControllerGrpc.UserActionControllerBlockingStub clientCollectorGrpc;

    @Override
    public RequestDto create(final long userId, final long eventId) {
        requestStorage.ifExistsByRequesterIdAndEventIdThenThrow(userId, eventId);
        UserDto userDto = adminUserClient.getAll(List.of(userId), 0, 1)
                .stream()
                .findFirst().orElseThrow(() -> new NotFoundException(User.class.getSimpleName(), userId));

        User user = cs.convert(userDto, User.class);

        EventDtoWithObjects eventDto = adminEventClient.getAll(null,
                        null,
                        null,
                        List.of(eventId),
                        null,
                        null,
                        0,
                        1)
                .stream()
                .findFirst().orElseThrow(() -> new NotFoundException(Event.class.getSimpleName(), eventId));

        Event event = cs.convert(eventDto, Event.class);

        if (event.getInitiator() == user.getId()) {
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
                .event(event.getId())
                .requester(user.getId())
                .status(event.getParticipantLimit() == 0 || !event.isRequestModeration() ? State.CONFIRMED
                        : State.PENDING)
                .build();

        if (request.getStatus() == State.CONFIRMED) {
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            UpdateEventDto updateEventDto = cs.convert(event, UpdateEventDto.class);
            adminEventClient.update(updateEventDto, event.getId());
        }

        UserActionProto userActionProto = UserActionProto.newBuilder()
                .setEventId(eventId)
                .setUserId(userId)
                .setActionType(ActionTypeProto.ACTION_REGISTER)
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(System.currentTimeMillis() / 1000)
                        .build())
                .build();

        clientCollectorGrpc.collectUserAction(userActionProto);

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
        adminUserClient.getAll(List.of(userId), 0, 1)
                .stream()
                .findFirst().orElseThrow(() -> new NotFoundException(User.class.getSimpleName(), userId));

        Request request = requestStorage.getByIdOrElseThrow(requestId);

        if (request.getStatus() == State.CONFIRMED) {
            EventDtoWithObjects eventDto = adminEventClient.getAll(null,
                    null,
                    null,
                    List.of(request.getEvent()),
                    null,
                    null,
                    null,
                    1)
                    .stream()
                    .findFirst().get();

            Event event = cs.convert(eventDto, Event.class);

            event.setConfirmedRequests(event.getConfirmedRequests() - 1);
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

            EventDtoWithObjects eventDto = adminEventClient.getAll(null,
                    null,
                    null,
                    List.of(request.getEvent()),
                    null,
                    null,
                    null,
                    1)
                    .stream()
                    .findFirst().get();

            Event event = cs.convert(eventDto, Event.class);

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

    @Override
    public List<RequestDto> getAllRequestsByEventInitiatorIdAndEventId(long userId, long eventId) {
        List<EventDtoWithObjects> eventDtos = adminEventClient.getAll(List.of(userId),
                null,
                null,
                List.of(eventId),
                null,
                null,
                null,
                Integer.MAX_VALUE);

        if (ObjectUtils.isEmpty(eventDtos)) {
            return Collections.emptyList();
        }

        return requestStorage.findAllByEventId(eventId)
                .stream()
                .map(request -> cs.convert(request, RequestDto.class))
                .toList();
    }
}
