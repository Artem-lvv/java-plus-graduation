package ru.yandex.practicum.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.event.model.PublicParameter;
import ru.yandex.practicum.event.model.dto.EventDtoWithObjects;
import ru.yandex.practicum.service.EventService;
import ru.yandex.practicum.validation.ConstraintNotZero;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class PublicEventController {
    private static final String SIMPLE_NAME = Event.class.getSimpleName();
    private final EventService eventService;

    @GetMapping("/{id}")
    public EventDtoWithObjects getById(@PathVariable @Positive final long id, final HttpServletRequest request,
                                       @RequestHeader("X-EWM-USER-ID") long userId) {
        log.info("Public event {} by id - {}", SIMPLE_NAME, id);
        return eventService.getById(id, request, userId);
    }

    @GetMapping
    public List<EventDtoWithObjects> getAll(@Valid final PublicParameter publicParameter,
                                 final HttpServletRequest request) {
        log.info("Public {} request with parameters - {}", SIMPLE_NAME, publicParameter);
        return eventService.getAll(publicParameter, request);
    }

    @GetMapping("/locations")
    public List<EventDtoWithObjects> getEventsByLatAndLon(@RequestParam @ConstraintNotZero final Double lat,
                                               @RequestParam @ConstraintNotZero final Double lon,
                                               @RequestParam(required = false, defaultValue = "0")
                                               @PositiveOrZero final double radius) {
        log.info("Public {} to receive events by location and radius - {} - {} - {}", SIMPLE_NAME, lat, lon, radius);
        return eventService.getAllByLocation(lat, lon, radius);
    }

    @GetMapping("/recommendations")
    public List<EventDtoWithObjects> getRecommendations(@RequestHeader("X-EWM-USER-ID") long userId) {
        log.info("Public event get recommendations {} by user id - {}", SIMPLE_NAME, userId);
        return eventService.getRecommendations(userId);
    }

    @PutMapping("/{eventId}/like")
    public void addLike(@PathVariable @Positive final long eventId,
                        @RequestHeader("X-EWM-USER-ID") long userId) {
        log.info("Public event add like {} by user id - {} and event id - {}", SIMPLE_NAME, userId, eventId);
        eventService.addLikeEvent(eventId, userId);
    }
}
