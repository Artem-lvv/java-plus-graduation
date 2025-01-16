package ru.yandex.practicum.event.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.location.model.dto.LocationLatAndLonDto;
import ru.yandex.practicum.validation.ConstraintFutureInTwoHours;

import java.time.LocalDateTime;

public record CreateEventDto(
        @Length(min = 20, max = 2000)
        @NotBlank
        String annotation,
        @Positive
        long category,
        @Length(min = 20, max = 7000)
        @NotBlank
        String description,
        @NotNull
        @ConstraintFutureInTwoHours
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime eventDate,
        @NotNull
        LocationLatAndLonDto location,
        boolean paid,
        @PositiveOrZero
        int participantLimit,
        Boolean requestModeration,
        @Length(min = 3, max = 120)
        @NotBlank
        String title
) {
}
