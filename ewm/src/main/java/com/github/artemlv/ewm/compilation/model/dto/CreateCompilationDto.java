package com.github.artemlv.ewm.compilation.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.Set;

@Builder
public record CreateCompilationDto(
        Set<Long> events,
        boolean pinned,
        @Size(max = 50)
        @NotBlank
        String title
) {
}
