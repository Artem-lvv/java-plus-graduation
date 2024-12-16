package com.github.artemlv.ewm.request.model.dto;

import com.github.artemlv.ewm.state.State;
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
