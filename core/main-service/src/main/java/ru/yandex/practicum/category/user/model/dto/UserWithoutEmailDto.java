package ru.yandex.practicum.category.user.model.dto;

import lombok.Builder;

@Builder
public record UserWithoutEmailDto(
        long id,
        String name
) {
}
