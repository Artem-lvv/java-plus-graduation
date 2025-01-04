package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.dto.CreateStatsDto;
import ru.yandex.practicum.dto.StatCountHitsDto;
import ru.yandex.practicum.dto.StatDto;
import ru.yandex.practicum.dto.StatsResponseHitDto;
import ru.yandex.practicum.model.Stat;

@Mapper(componentModel = "spring")
public abstract class StatsMapper {

    @Mapping(target = "id", ignore = true)
    public abstract StatDto toStatDto(CreateStatsDto saveRequestDto);

    public abstract Stat toStat(StatDto statDto);

    public abstract StatsResponseHitDto toResponseDto(Stat stat);

    public abstract StatCountHitsDto toCountHitsResponseDto(StatCountHitsDto countHitsDto);


}
