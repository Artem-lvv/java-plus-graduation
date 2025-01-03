package ru.yandex.practicum.category.request.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import ru.yandex.practicum.category.state.State;
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
