package ru.yandex.practicum.compilation.model.dto;

import lombok.Builder;
import ru.yandex.practicum.event.model.dto.EventDtoWithObjects;

import java.util.List;

@Builder
public record CompilationDto(
        long id,
        List<EventDtoWithObjects> events,
        boolean pinned,
        String title
) {
}
