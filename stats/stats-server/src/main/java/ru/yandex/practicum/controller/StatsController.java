package ru.yandex.practicum.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.StatCountHitsDto;
import ru.yandex.practicum.dto.StatsResponseHitDto;
import ru.yandex.practicum.dto.CreateStatsDto;
import ru.yandex.practicum.SearchStats;
import ru.yandex.practicum.service.StatsService;

import java.util.List;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
public class StatsController {
    public static final String STATS_PATH = "/stats";
    public static final String HIT_PATH = "/hit";
    private final StatsService statsService;

    @PostMapping(HIT_PATH)
    @ResponseStatus(HttpStatus.CREATED)
    public StatsResponseHitDto save(@RequestBody @Valid CreateStatsDto saveRequestDto) {
        log.info("Save request {}", saveRequestDto);
        return statsService.save(saveRequestDto);
    }

    @GetMapping(STATS_PATH)
    public List<StatCountHitsDto> get(@Valid SearchStats searchStats) {
        log.info("Request to display statistics uris {}", searchStats);
        return statsService.getStats(searchStats);
    }
}
