package com.github.artemlv.ewm.compilation.model.dto;

import com.github.artemlv.ewm.event.model.dto.EventDto;
import lombok.Builder;

import java.util.List;

@Builder
public record CompilationDto(
        long id,
        List<EventDto> events,
        boolean pinned,
        String title
) {
}
