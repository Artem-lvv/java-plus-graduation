package ru.yandex.practicum.user.model.dto;

import lombok.Builder;

@Builder
public record UserWithoutEmailDto(
        long id,
        String name
) {
}
