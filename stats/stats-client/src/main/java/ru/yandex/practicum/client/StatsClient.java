package ru.yandex.practicum.client;

import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.SearchStats;
import ru.yandex.practicum.dto.CreateStatsDto;
import ru.yandex.practicum.dto.StatCountHitsDto;

import java.util.List;

public interface StatsClient {
    List<StatCountHitsDto> get(final SearchStats searchStats);

    ResponseEntity<Void> save(final CreateStatsDto request);
}
