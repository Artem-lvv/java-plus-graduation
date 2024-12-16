package ru.yandex.practicum.storage.database;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.StatCountHitsDto;
import ru.yandex.practicum.model.Stat;
import ru.yandex.practicum.storage.StatsStorage;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsStorageImpl implements StatsStorage {
    private final StatsRepository statsRepository;

    @Override
    @Transactional
    public Stat save(Stat stat) {
        final Stat saved = statsRepository.save(stat);
        log.info("Saved stat: {}", saved);
        return saved;
    }

    @Override
    public List<StatCountHitsDto> findAllWithUniqueIp(final LocalDateTime start, final LocalDateTime end) {
        final List<StatCountHitsDto> allWithUniqueIp = statsRepository.findAllWithUniqueIp(start, end);
        log.info("Get all with unique ip: {}", allWithUniqueIp);
        return allWithUniqueIp;
    }

    @Override
    public List<StatCountHitsDto> findWithUniqueIp(final LocalDateTime start, final LocalDateTime end,
                                                   final List<String> uris) {
        final List<StatCountHitsDto> withUniqueIp = statsRepository.findWithUniqueIp(start, end, uris);
        log.info("Get all with unique ip with uris: {}, {}", withUniqueIp, uris);
        return withUniqueIp;
    }

    @Override
    public List<StatCountHitsDto> findAllWithoutUniqueIp(final LocalDateTime start, final LocalDateTime end) {
        final List<StatCountHitsDto> allWithoutUniqueIp = statsRepository.findAllWithoutUniqueIp(start, end);
        log.info("Get all without unique ip: {}", allWithoutUniqueIp);
        return allWithoutUniqueIp;
    }

    @Override
    public List<StatCountHitsDto> findWithoutUniqueIp(final LocalDateTime start, final LocalDateTime end,
                                                      final List<String> uris) {
        final List<StatCountHitsDto> withoutUniqueIp = statsRepository.findWithoutUniqueIp(start, end, uris);
        log.info("Get all without unique ip with uris: {}, {}", withoutUniqueIp, uris);
        return withoutUniqueIp;
    }
}
