package ru.yandex.practicum.request.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import ru.yandex.practicum.state.State;

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
