package ru.yandex.practicum;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import ru.yandex.practicum.event.model.dto.EventDto;
import ru.yandex.practicum.event.model.dto.UpdateEventDto;
import ru.yandex.practicum.event.model.AdminParameter;

import java.util.List;

@FeignClient(name = "${admin.events.service.name:EVENT-SERVICE}", url = "${admin.events.service.url}")
public interface AdminEventClient {

    @GetMapping("/admin/events")
    List<EventDto> getAll(@Valid @RequestBody AdminParameter adminParameter);

    @PatchMapping("/admin/events/{eventId}")
    EventDto update(@RequestBody @Valid UpdateEventDto updateEventDto,
                    @PathVariable @Positive long eventId);
}
