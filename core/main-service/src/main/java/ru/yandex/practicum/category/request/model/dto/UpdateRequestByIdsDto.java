package ru.yandex.practicum.category.request.model.dto;

import ru.yandex.practicum.category.state.State;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Set;

@Builder
public record UpdateRequestByIdsDto(
        @NotEmpty
        Set<Long> requestIds,
        @NotNull
        State status
) {
}
