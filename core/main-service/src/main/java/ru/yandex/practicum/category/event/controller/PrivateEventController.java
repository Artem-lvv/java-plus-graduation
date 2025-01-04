package ru.yandex.practicum.category.event.controller;

import ru.yandex.practicum.category.event.model.Event;
import ru.yandex.practicum.category.event.model.dto.CreateEventDto;
import ru.yandex.practicum.category.event.model.dto.EventDto;
import ru.yandex.practicum.category.event.model.dto.UpdateEventDto;
import ru.yandex.practicum.category.event.service.EventService;
import ru.yandex.practicum.category.request.model.dto.RequestDto;
import ru.yandex.practicum.category.request.model.dto.RequestStatusUpdateResultDto;
import ru.yandex.practicum.category.request.model.dto.UpdateRequestByIdsDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
public class PrivateEventController {
    private static final String SIMPLE_NAME = Event.class.getSimpleName();
    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto create(@RequestBody @Valid final CreateEventDto createEventDto,
                           @PathVariable @Positive final long userId) {
        log.info("Request to create an {} - {}", SIMPLE_NAME, createEventDto);
        return eventService.create(createEventDto, userId);
    }

    @GetMapping
    public List<EventDto> getAllByUserId(@PathVariable @Positive final long userId,
                                         @RequestParam(defaultValue = "0") @PositiveOrZero final int from,
                                         @RequestParam(defaultValue = "10") @Positive final int size) {
        log.info("Request for user {} by id - {} start - {} size - {}", SIMPLE_NAME, userId, from, size);
        return eventService.getAllByUserId(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventDto getByIdAndUserId(@PathVariable @Positive final long userId,
                                     @PathVariable @Positive final long eventId) {
        log.info("{} request by id - {} by user with id - {}", SIMPLE_NAME, eventId, userId);
        return eventService.getByIdAndUserId(eventId, userId);
    }

    @GetMapping("/{eventId}/requests")
    public List<RequestDto> getRequestsByUserIdAndEventId(@PathVariable @Positive final long userId,
                                                          @PathVariable @Positive final long eventId) {
        log.info("Request to receive requests for participation in an {} by id - {} by user by id - {}",
                SIMPLE_NAME, eventId, userId);
        return eventService.getRequestsByUserIdAndEventId(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public RequestStatusUpdateResultDto updateRequestsByUserAndEvent(
            @RequestBody final UpdateRequestByIdsDto updateEventStatusByRequestIds,
            @PathVariable @Positive final long userId,
            @PathVariable @Positive final long eventId) {
        log.info("Request to update the status of requests for an {} by id - {} by user with id - {} - {}",
                SIMPLE_NAME, eventId, userId, updateEventStatusByRequestIds);
        return eventService.updateRequestsStatusByUserIdAndEventId(userId, eventId, updateEventStatusByRequestIds);
    }

    @PatchMapping("/{eventId}")
    public EventDto update(@PathVariable @Positive final long userId,
                           @PathVariable @Positive final long eventId,
                           @RequestBody @Valid UpdateEventDto updateEventDto) {
        log.info("Request to update {} {} eventId = {}", SIMPLE_NAME, updateEventDto, eventId);
        return eventService.updateByUser(userId, eventId, updateEventDto);
    }

}
