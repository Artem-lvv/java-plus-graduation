package com.github.artemlv.ewm.user.model.dto;

import lombok.Builder;

@Builder
public record UserWithoutEmailDto(
        long id,
        String name
) {
}
