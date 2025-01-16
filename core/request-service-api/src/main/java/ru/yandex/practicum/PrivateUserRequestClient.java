package ru.yandex.practicum;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.Positive;
import ru.yandex.practicum.request.model.dto.RequestDto;
import ru.yandex.practicum.request.model.dto.RequestStatusUpdateResultDto;
import ru.yandex.practicum.request.model.dto.UpdateRequestByIdsDto;

import java.util.List;

@FeignClient(name = "${admin.request.service.name:REQUEST-SERVICE}", url = "${private.user.requests.service.url}")
public interface PrivateUserRequestClient {

    @PostMapping("/users/{userId}/requests")
    RequestDto createRequest(@PathVariable @Positive long userId,
                             @RequestParam @Positive long eventId);

    @GetMapping("/users/{userId}/requests")
    List<RequestDto> getAllRequests(@PathVariable @Positive long userId);

    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    RequestDto cancelRequest(@PathVariable @Positive long userId,
                             @PathVariable @Positive long requestId);

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    List<RequestDto> getRequestsByUserIdAndEventId(@PathVariable @Positive long userId,
                                                   @PathVariable @Positive long eventId);

    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    RequestStatusUpdateResultDto updateRequestsByUserAndEvent(
            @RequestBody UpdateRequestByIdsDto updateRequestByIdsDto,
            @PathVariable @Positive long userId,
            @PathVariable @Positive long eventId);

    @GetMapping("requests/events/{eventId}")
    List<RequestDto> getRequestsByEventId(@PathVariable @Positive long eventId);
}
