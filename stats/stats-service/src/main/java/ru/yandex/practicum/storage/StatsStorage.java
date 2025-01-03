package ru.yandex.practicum.storage;

import ru.yandex.practicum.dto.StatCountHitsDto;
import ru.yandex.practicum.model.Stat;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsStorage {

    Stat save(Stat stat);

    List<StatCountHitsDto> findAllWithUniqueIp(final LocalDateTime start, final LocalDateTime end);

    List<StatCountHitsDto> findWithUniqueIp(final LocalDateTime start, final LocalDateTime end,
                                            final List<String> uris);

    List<StatCountHitsDto> findAllWithoutUniqueIp(final LocalDateTime start, final LocalDateTime end);

    List<StatCountHitsDto> findWithoutUniqueIp(final LocalDateTime start, final LocalDateTime end,
                                               final List<String> uris);
}
