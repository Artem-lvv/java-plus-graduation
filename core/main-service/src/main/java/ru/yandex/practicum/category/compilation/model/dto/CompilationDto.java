package ru.yandex.practicum.category.compilation.model.dto;

import ru.yandex.practicum.category.event.model.dto.EventDto;
import lombok.Builder;

import java.util.List;

@Builder
public record CompilationDto(
        long id,
        List<EventDto> events,
        boolean pinned,
        String title
) {
}
