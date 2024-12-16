package com.github.artemlv.ewm.category.model.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record CreateCategoryDto(
        @Length(max = 50)
        @NotBlank
        String name
) {
}
