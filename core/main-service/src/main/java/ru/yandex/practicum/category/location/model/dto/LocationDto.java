package ru.yandex.practicum.category.location.model.dto;

import lombok.Builder;

@Builder
public record LocationDto(
        long id,
        double lat,
        double lon,
        double radius,
        String name
) {
}
