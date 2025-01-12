package ru.yandex.practicum;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import ru.yandex.practicum.event.model.dto.EventDto;
import ru.yandex.practicum.event.model.dto.UpdateEventDto;
import ru.yandex.practicum.event.model.AdminParameter;

import java.util.List;

@FeignClient(name = "${admin.events.service.name:EVENT-SERVICE}",
        url = "${admin.events.service.url}"
)
public interface AdminEventClient {

    @GetMapping("/admin/events")
    List<EventDto> getAll(@RequestParam(value = "users", required = false) List<Long> users,
                          @RequestParam(value = "states", required = false) List<String> states,
                          @RequestParam(value = "categories", required = false) List<Long> categories,
                          @RequestParam(value = "events", required = false) List<Long> events,
                          @RequestParam(value = "rangeStart", required = false) String rangeStart,
                          @RequestParam(value = "rangeEnd", required = false) String rangeEnd,
                          @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
                          @RequestParam(value = "size", required = false, defaultValue = "10") Integer size);

    @PatchMapping("/admin/events/{eventId}")
    EventDto update(@RequestBody UpdateEventDto updateEventDto,
                    @PathVariable("eventId") long eventId);
}
