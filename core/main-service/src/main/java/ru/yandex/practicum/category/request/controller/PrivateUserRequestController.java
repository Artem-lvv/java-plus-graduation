package ru.yandex.practicum.category.request.controller;

import ru.yandex.practicum.category.request.model.Request;
import ru.yandex.practicum.category.request.model.dto.RequestDto;
import ru.yandex.practicum.category.request.service.RequestService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
public class PrivateUserRequestController {
    private static final String SIMPLE_NAME = Request.class.getSimpleName();
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto create(@PathVariable @Positive final long userId,
                             @RequestParam @Positive final long eventId) {
        log.info("{} to participate in an event by id - {} by user with id - {}", SIMPLE_NAME, eventId, userId);
        return requestService.create(userId, eventId);
    }

    @GetMapping
    public List<RequestDto> getAll(@PathVariable @Positive final long userId) {
        log.info("{} user participation by id - {}", SIMPLE_NAME, userId);
        return requestService.getAll(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    public RequestDto cancel(@PathVariable @Positive final long userId,
                             @PathVariable @Positive final long requestId) {
        log.info("{} to cancel participation by id - {} of user with id - {}", SIMPLE_NAME, requestId, userId);
        return requestService.cancel(userId, requestId);
    }
}
