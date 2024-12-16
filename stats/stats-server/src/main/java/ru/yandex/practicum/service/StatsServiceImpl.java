package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import ru.yandex.practicum.SearchStats;
import ru.yandex.practicum.dto.CreateStatsDto;
import ru.yandex.practicum.dto.StatCountHitsDto;
import ru.yandex.practicum.dto.StatDto;
import ru.yandex.practicum.dto.StatsResponseHitDto;
import ru.yandex.practicum.mapper.StatsMapper;
import ru.yandex.practicum.model.Stat;
import ru.yandex.practicum.storage.StatsStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsStorage statsStorage;
    private final StatsMapper statsMapper;

    @Override
    public StatsResponseHitDto save(final CreateStatsDto createStatsDto) {
        StatDto statDto = statsMapper.toStatDto(createStatsDto);
        Stat stat = statsMapper.toStat(statDto);
        stat = statsStorage.save(stat);

        return statsMapper.toResponseDto(stat);
    }

    @Override
    public List<StatCountHitsDto> getStats(SearchStats searchStats) {
        List<StatCountHitsDto> listStats;

        if (searchStats.isUnique()) {
            listStats = ObjectUtils.isEmpty(searchStats.getUris())
                    ? statsStorage.findAllWithUniqueIp(searchStats.getStart(), searchStats.getEnd())
                    : statsStorage.findWithUniqueIp(searchStats.getStart(), searchStats.getEnd(), searchStats.getUris());
        } else {
            listStats = ObjectUtils.isEmpty(searchStats.getUris())
                    ? statsStorage.findAllWithoutUniqueIp(searchStats.getStart(), searchStats.getEnd())
                    : statsStorage.findWithoutUniqueIp(searchStats.getStart(), searchStats.getEnd(), searchStats.getUris());
        }

        return listStats.stream()
                .map(statsMapper::toCountHitsResponseDto)
                .toList();
    }
}
