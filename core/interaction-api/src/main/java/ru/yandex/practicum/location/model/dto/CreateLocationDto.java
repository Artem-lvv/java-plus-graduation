package ru.yandex.practicum.location.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import ru.yandex.practicum.validation.ConstraintNotZero;

@Builder
public record CreateLocationDto(
        @ConstraintNotZero
        Double lat,
        @ConstraintNotZero
        Double lon,
        @PositiveOrZero
        double radius,
        @Size(max = 255)
        @NotBlank
        String name
) {
}
