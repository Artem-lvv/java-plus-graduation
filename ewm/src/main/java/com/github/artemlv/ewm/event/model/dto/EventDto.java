package com.github.artemlv.ewm.event.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.artemlv.ewm.category.model.dto.CategoryDto;
import com.github.artemlv.ewm.location.model.dto.LocationDto;
import com.github.artemlv.ewm.state.State;
import com.github.artemlv.ewm.user.model.dto.UserWithoutEmailDto;
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
