package ru.yandex.practicum.location.model.dto;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UpdateLocationDto(
        double lat,
        double lon,
        @PositiveOrZero
        double radius,
        @Size(max = 255)
        String name
) {
}
