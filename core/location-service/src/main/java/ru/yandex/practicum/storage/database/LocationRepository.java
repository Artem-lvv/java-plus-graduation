package ru.yandex.practicum.storage.database;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.location.model.Location;

import java.util.List;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {

    Optional<Location> findByLatAndLon(final double lat, final double lon);

    List<Location> findAllByNameContainingIgnoreCase(final String text, final PageRequest page);

    List<Location> findByLatAndLonAndRadius(final double lat, final double lon, final double radius);
}
