package ru.yandex.practicum.category.compilation.model.dto;

import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.Set;

@Builder
public record UpdateCompilationDto(
        Set<Long> events,
        Boolean pinned,
        @Size(max = 50)
        String title
) {
}
