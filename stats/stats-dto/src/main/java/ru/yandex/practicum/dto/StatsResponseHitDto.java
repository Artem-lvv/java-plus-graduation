package ru.yandex.practicum.dto;

public record StatsResponseHitDto(
        String app,
        String uri,
        String ip
) {
}
