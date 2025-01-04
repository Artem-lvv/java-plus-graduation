package ru.yandex.practicum.category.event.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import ru.yandex.practicum.category.model.dto.CategoryDto;
import ru.yandex.practicum.category.location.model.dto.LocationDto;
import ru.yandex.practicum.category.state.State;
import ru.yandex.practicum.category.user.model.dto.UserWithoutEmailDto;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record EventDto(
        long id,
        String annotation,
        CategoryDto category,
        int confirmedRequests,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdOn,
        String description,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime eventDate,
        UserWithoutEmailDto initiator,
        LocationDto location,
        boolean paid,
        int participantLimit,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime publishedOn,
        boolean requestModeration,
        State state,
        String title,
        long views
) {
}
