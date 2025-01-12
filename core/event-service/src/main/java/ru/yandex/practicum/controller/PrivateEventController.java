package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.event.model.dto.CreateEventDto;
import ru.yandex.practicum.event.model.dto.EventDto;
import ru.yandex.practicum.event.model.dto.UpdateEventDto;
import ru.yandex.practicum.request.model.dto.RequestDto;
import ru.yandex.practicum.request.model.dto.RequestStatusUpdateResultDto;
import ru.yandex.practicum.request.model.dto.UpdateRequestByIdsDto;
import ru.yandex.practicum.service.EventService;

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



    @PatchMapping("/{eventId}")
    public EventDto update(@PathVariable @Positive final long userId,
                           @PathVariable @Positive final long eventId,
                           @RequestBody @Valid UpdateEventDto updateEventDto) {
        log.info("Request to update {} {} eventId = {}", SIMPLE_NAME, updateEventDto, eventId);
        return eventService.updateByUser(userId, eventId, updateEventDto);
    }

}
