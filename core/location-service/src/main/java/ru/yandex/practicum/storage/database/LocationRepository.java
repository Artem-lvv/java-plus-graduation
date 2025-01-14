package ru.yandex.practicum.storage.database;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.location.model.Location;

import java.util.List;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {

    Optional<Location> findByLatAndLon(final double lat, final double lon);

    List<Location> findAllByNameContainingIgnoreCase(final String text, final PageRequest page);

    @Query(value = "SELECT l.* " +
            "FROM locations l " +
            "WHERE distance(l.lat, l.lon, :lat, :lon) < :radius", nativeQuery = true)
    List<Location> findByLatAndLonAndRadius(@Param("lat") final double lat,
                                            @Param("lon") final double lon,
                                            @Param("radius") final double radius);

}
