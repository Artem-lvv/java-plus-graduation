package ru.yandex.practicum.request.model.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import ru.yandex.practicum.state.State;

import java.util.Set;

@Builder
public record UpdateRequestByIdsDto(
        @NotEmpty
        Set<Long> requestIds,
        @NotNull
        State status
) {
}
