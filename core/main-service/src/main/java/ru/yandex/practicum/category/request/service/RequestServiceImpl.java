package ru.yandex.practicum.category.request.service;


import ru.yandex.practicum.category.event.model.Event;
import ru.yandex.practicum.category.event.storage.EventStorage;
import ru.yandex.practicum.category.exception.type.ConflictException;
import ru.yandex.practicum.category.request.model.Request;
import ru.yandex.practicum.category.request.model.dto.RequestDto;
import ru.yandex.practicum.category.request.storage.RequestStorage;
import ru.yandex.practicum.category.state.State;
import ru.yandex.practicum.category.user.model.User;
import ru.yandex.practicum.category.user.storage.UserStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private static final String SIMPLE_NAME = Request.class.getSimpleName();
    @Qualifier("mvcConversionService")
    private final ConversionService cs;
    private final RequestStorage requestStorage;
    private final UserStorage userStorage;
    private final EventStorage eventStorage;

    @Override
    public RequestDto create(final long userId, final long eventId) {
        requestStorage.ifExistsByRequesterIdAndEventIdThenThrow(userId, eventId);
        User user = userStorage.getByIdOrElseThrow(userId);
        Event event = eventStorage.getByIdOrElseThrow(eventId);

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
            eventStorage.save(event);
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
        userStorage.existsByIdOrElseThrow(userId);
        Request request = requestStorage.getByIdOrElseThrow(requestId);

        if (request.getStatus() == State.CONFIRMED) {
            Event event = request.getEvent();
            event.setConfirmedRequests(event.getConfirmedRequests() - 1);
            eventStorage.save(event);
        }

        request.setStatus(State.CANCELED);

        return cs.convert(requestStorage.save(request), RequestDto.class);
    }
}
