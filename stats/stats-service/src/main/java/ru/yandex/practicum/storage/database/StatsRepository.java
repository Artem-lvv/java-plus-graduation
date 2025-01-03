package ru.yandex.practicum.storage.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.dto.StatCountHitsDto;
import ru.yandex.practicum.model.Stat;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<Stat, Long> {
    @Query("SELECT new ru.yandex.practicum.dto.StatCountHitsDto(s.app, s.uri, "
            + "(SELECT count(st.ip) FROM Stat AS st WHERE st.uri = s.uri) AS hits) "
            + "FROM Stat AS s WHERE s.uri IN ( ?3 ) AND s.timestamp BETWEEN ?1 AND ?2 "
            + "GROUP BY s.uri, s.app ORDER BY hits DESC ")
    List<StatCountHitsDto> findWithoutUniqueIp(final LocalDateTime start, final LocalDateTime end,
                                               final List<String> uris);

    @Query("SELECT new ru.yandex.practicum.dto.StatCountHitsDto(s.app, s.uri, "
            + "(SELECT count(DISTINCT st.ip) FROM Stat AS st WHERE st.uri = s.uri) AS hits) "
            + "FROM Stat AS s WHERE s.uri IN ( ?3 ) AND s.timestamp BETWEEN ?1 AND ?2 "
            + "GROUP BY s.uri, s.app ORDER BY hits DESC ")
    List<StatCountHitsDto> findWithUniqueIp(final LocalDateTime start, final LocalDateTime end, final List<String> uris);

    @Query("SELECT new ru.yandex.practicum.dto.StatCountHitsDto(s.app, s.uri, "
            + "(SELECT count(st.ip) FROM Stat AS st WHERE st.uri = s.uri) AS hits) "
            + "FROM Stat AS s WHERE s.timestamp BETWEEN ?1 AND ?2 GROUP BY s.uri, s.app ORDER BY hits DESC ")
    List<StatCountHitsDto> findAllWithoutUniqueIp(final LocalDateTime start, final LocalDateTime end);

    @Query("SELECT new ru.yandex.practicum.dto.StatCountHitsDto(s.app, s.uri, "
            + "(SELECT count(DISTINCT st.ip) FROM Stat AS st WHERE st.uri = s.uri) AS hits) "
            + "FROM Stat AS s WHERE s.timestamp BETWEEN ?1 AND ?2 GROUP BY s.uri, s.app ORDER BY hits DESC ")
    List<StatCountHitsDto> findAllWithUniqueIp(final LocalDateTime start, final LocalDateTime end);

}
