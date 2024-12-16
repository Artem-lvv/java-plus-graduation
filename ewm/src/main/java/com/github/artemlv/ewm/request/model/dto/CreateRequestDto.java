package com.github.artemlv.ewm.request.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.artemlv.ewm.state.State;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CreateRequestDto(
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime created,
        @PositiveOrZero
        long event,
        @PositiveOrZero
        long requester,
        State status
) {
}
