package ru.yandex.practicum.user.model.dto;

import lombok.Builder;

@Builder
public record UserDto(
        long id,
        String email,
        String name
) {
}
