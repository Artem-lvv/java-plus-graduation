package ru.yandex.practicum.dto;


import java.time.LocalDateTime;

public record StatDto(
        long id,
        String app,
        String uri,
        String ip,
        LocalDateTime timestamp
) {
}
