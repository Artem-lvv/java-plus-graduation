package ru.yandex.practicum.category.event.controller;

import ru.yandex.practicum.category.event.model.AdminParameter;
import ru.yandex.practicum.category.event.model.Event;
import ru.yandex.practicum.category.event.model.dto.EventDto;
import ru.yandex.practicum.category.event.model.dto.UpdateEventDto;
import ru.yandex.practicum.category.event.service.EventService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class AdminEventController {
    private static final String SIMPLE_NAME = Event.class.getSimpleName();
    private final EventService eventService;

    @GetMapping
    public List<EventDto> getAll(@Valid final AdminParameter adminParameter) {
        log.info("Administrator's request to provide {} by parameters - {}", SIMPLE_NAME, adminParameter);
        return eventService.getAllByAdmin(adminParameter);
    }

    @PatchMapping("/{eventId}")
    public EventDto update(@RequestBody @Valid final UpdateEventDto updateEventDto,
                           @PathVariable @Positive final long eventId) {
        log.info("Request by the administrator to change an {} by id - {} - {}", SIMPLE_NAME, eventId, updateEventDto);
        return eventService.updateByAdmin(eventId, updateEventDto);
    }
}
