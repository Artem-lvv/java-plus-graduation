package ru.yandex.practicum.request.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import ru.yandex.practicum.state.State;

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
