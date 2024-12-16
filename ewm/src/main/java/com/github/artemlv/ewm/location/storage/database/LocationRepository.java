package com.github.artemlv.ewm.location.storage.database;

import com.github.artemlv.ewm.location.model.Location;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {

    Optional<Location> findByLatAndLon(final double lat, final double lon);

    List<Location> findAllByNameContainingIgnoreCase(final String text, final PageRequest page);

    List<Location> findByLatAndLonAndRadius(final double lat, final double lon, final double radius);
}
