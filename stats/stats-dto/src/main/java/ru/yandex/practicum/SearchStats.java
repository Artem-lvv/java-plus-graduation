package ru.yandex.practicum;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ru.yandex.practicum.validation.CheckSearchStatsDate;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@CheckSearchStatsDate
public class SearchStats {
    private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";

    @NotNull
    @DateTimeFormat(pattern = PATTERN)
    private LocalDateTime start;
    @NotNull
    @DateTimeFormat(pattern = PATTERN)
    private LocalDateTime end;
    private List<String> uris;
    private boolean unique;
}
