package ru.yandex.practicum.event.model;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ru.yandex.practicum.state.State;
import ru.yandex.practicum.validation.EventStartDateBeforeEndDate;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EventStartDateBeforeEndDate
public class AdminParameter {
    private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";
    private List<Long> users;
    private List<State> states;
    private List<Long> categories;
    private List<Long> events;

    @JsonSetter(nulls = Nulls.SKIP)
    @DateTimeFormat(pattern = PATTERN)
    private LocalDateTime rangeStart = LocalDateTime.of(1969, 10, 29, 0, 0);

    @JsonSetter(nulls = Nulls.SKIP)
    @DateTimeFormat(pattern = PATTERN)
    private LocalDateTime rangeEnd = LocalDateTime.of(2169, 10, 29, 0, 0);

    @PositiveOrZero
    private int from = 0;

    @Positive
    private int size = 10;
}
