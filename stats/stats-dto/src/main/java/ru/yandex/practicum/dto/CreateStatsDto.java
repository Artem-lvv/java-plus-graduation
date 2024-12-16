package ru.yandex.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CreateStatsDto(
        @Size(max = 255)
        @NotBlank
        String app,
        @Size(max = 255)
        @NotBlank
        String uri,
        @Size(max = 45)
        @NotBlank
        String ip,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime timestamp
) {
}
