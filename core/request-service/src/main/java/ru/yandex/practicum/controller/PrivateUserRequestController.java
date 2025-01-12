package ru.yandex.practicum.controller;

import ru.yandex.practicum.request.model.Request;
import ru.yandex.practicum.request.model.dto.RequestDto;
import ru.yandex.practicum.request.model.dto.RequestStatusUpdateResultDto;
import ru.yandex.practicum.request.model.dto.UpdateRequestByIdsDto;
import ru.yandex.practicum.service.RequestService;
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
@RequestMapping()
@RequiredArgsConstructor
public class PrivateUserRequestController {
    private static final String SIMPLE_NAME = Request.class.getSimpleName();
    private final RequestService requestService;

    @PostMapping("/users/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto create(@PathVariable @Positive final long userId,
                             @RequestParam @Positive final long eventId) {
        log.info("{} to participate in an event by id - {} by user with id - {}", SIMPLE_NAME, eventId, userId);
        return requestService.create(userId, eventId);
    }

    @GetMapping("/users/{userId}/requests")
    public List<RequestDto> getAll(@PathVariable @Positive final long userId) {
        log.info("{} user participation by id - {}", SIMPLE_NAME, userId);
        return requestService.getAll(userId);
    }

    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    public RequestDto cancel(@PathVariable @Positive final long userId,
                             @PathVariable @Positive final long requestId) {
        log.info("{} to cancel participation by id - {} of user with id - {}", SIMPLE_NAME, requestId, userId);
        return requestService.cancel(userId, requestId);
    }

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    public List<RequestDto> getRequestsByUserIdAndEventId(@PathVariable @Positive final long userId,
                                                          @PathVariable @Positive final long eventId) {
        log.info("Request to receive requests for participation in an {} by id - {} by user by id - {}",
                SIMPLE_NAME, eventId, userId);
        return requestService.getRequestsByUserIdAndEventId(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    public RequestStatusUpdateResultDto updateRequestsByUserAndEvent(
            @RequestBody final UpdateRequestByIdsDto updateRequestByIdsDto,
            @PathVariable @Positive final long userId,
            @PathVariable @Positive final long eventId) {
        log.info("Request to update the status of requests for an {} by id - {} by user with id - {} - {}",
                SIMPLE_NAME, eventId, userId, updateRequestByIdsDto);
        return requestService.updateRequestsStatusByUserIdAndEventId(userId, eventId, updateRequestByIdsDto);
    }

    @GetMapping("requests/events/{eventId}")
    public List<RequestDto> getRequestsByEventId(@PathVariable @Positive final long eventId) {
        log.info("Request to receive requests for an {} by id - {}", SIMPLE_NAME, eventId);
        return requestService.getRequestsByEventId(eventId);
    }
}
