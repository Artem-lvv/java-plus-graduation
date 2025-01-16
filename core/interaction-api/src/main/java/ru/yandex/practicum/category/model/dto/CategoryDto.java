package ru.yandex.practicum.category.model.dto;

import lombok.Builder;

@Builder
public record CategoryDto(
        long id,
        String name
) {
}
