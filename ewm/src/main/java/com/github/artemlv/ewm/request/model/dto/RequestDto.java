package com.github.artemlv.ewm.request.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.artemlv.ewm.state.State;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record RequestDto(
        long id,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime created,
        long event,
        long requester,
        State status
) {

}
