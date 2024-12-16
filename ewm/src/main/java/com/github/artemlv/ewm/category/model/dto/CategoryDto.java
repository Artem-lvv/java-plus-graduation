package com.github.artemlv.ewm.category.model.dto;

import lombok.Builder;

@Builder
public record CategoryDto(
        long id,
        String name
) {
}
