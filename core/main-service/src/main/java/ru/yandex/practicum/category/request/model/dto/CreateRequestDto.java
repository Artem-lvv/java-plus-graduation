package ru.yandex.practicum.category.request.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import ru.yandex.practicum.category.state.State;
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
