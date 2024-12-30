package ru.yandex.practicum.category.location.model.dto;

import lombok.Builder;

@Builder
public record LocationLatAndLonDto(
        double lat,
        double lon
) {
}