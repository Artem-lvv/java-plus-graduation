package ru.yandex.practicum.event.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import ru.yandex.practicum.state.State;

import java.time.LocalDateTime;

@Builder
public record EventDto(
        long id,
        String annotation,
        long category,
        int confirmedRequests,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdOn,
        String description,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime eventDate,
        long initiator,
        long location,
        boolean paid,
        int participantLimit,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime publishedOn,
        boolean requestModeration,
        State state,
        String title,
        double rating) {
}
