package ru.yandex.practicum;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.Positive;
import ru.yandex.practicum.request.model.dto.RequestDto;

import java.util.List;

@FeignClient(name = "${admin.request.service.name:REQUEST-SERVICE}", url = "${private.user.requests.service.url}")
public interface PrivateUserRequestClient {

    @PostMapping("/users/{userId}/requests")
    RequestDto create(@PathVariable @Positive long userId,
                      @RequestParam @Positive long eventId);

    @GetMapping("/users/{userId}/requests")
    List<RequestDto> getAll(@PathVariable @Positive long userId);

    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    RequestDto cancel(@PathVariable @Positive long userId,
                      @PathVariable @Positive long requestId);
}
