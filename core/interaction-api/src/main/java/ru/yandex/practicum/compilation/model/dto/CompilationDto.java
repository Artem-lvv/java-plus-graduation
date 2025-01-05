package ru.yandex.practicum.compilation.model.dto;

import lombok.Builder;
import ru.yandex.practicum.event.model.dto.EventDto;

import java.util.List;

@Builder
public record CompilationDto(
        long id,
        List<EventDto> events,
        boolean pinned,
        String title
) {
}
