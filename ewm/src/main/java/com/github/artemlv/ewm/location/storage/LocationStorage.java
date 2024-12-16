package com.github.artemlv.ewm.location.storage;

import com.github.artemlv.ewm.location.model.Location;

import java.util.List;
import java.util.Optional;

public interface LocationStorage {
    Location save(Location location);

    Optional<Location> findByLatAndLon(final double lat, final double lon);

    Optional<Location> findById(final long id);

    Location findByIdOrElseThrow(final long id);

    void existsByIdOrElseThrow(final long id);

    void deleteById(final long id);

    List<Location> findAllByNameContainingIgnoreCase(final String text, final int from, final int size);
}
