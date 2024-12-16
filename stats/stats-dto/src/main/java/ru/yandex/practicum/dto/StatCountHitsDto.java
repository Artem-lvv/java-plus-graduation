package ru.yandex.practicum.dto;

public record StatCountHitsDto(
        String app,
        String uri,
        long hits

) {
}
