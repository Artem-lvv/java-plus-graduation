package com.github.artemlv.ewm.location.model.dto;

import com.github.artemlv.ewm.location.validation.ConstraintNotZero;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;

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
