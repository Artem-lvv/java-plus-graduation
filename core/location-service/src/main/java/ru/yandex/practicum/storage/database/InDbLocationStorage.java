package ru.yandex.practicum.storage.database;

import ru.yandex.practicum.exception.type.NotFoundException;
import ru.yandex.practicum.location.model.Location;
import ru.yandex.practicum.storage.LocationStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InDbLocationStorage implements LocationStorage {
    private static final String SIMPLE_NAME = Location.class.getSimpleName();
    private final LocationRepository locationRepository;

    @Override
    @Transactional
    public Location save(Location location) {
        final Location savedLocation = locationRepository.save(location);
        log.info("Save {} - {}", SIMPLE_NAME, savedLocation);
        return savedLocation;
    }

    @Override
    public Optional<Location> findByLatAndLon(final double lat, final double lon) {
        final Optional<Location> location = locationRepository.findByLatAndLon(lat, lon);
        log.info("Get Optional<{}> by lat and lon - {}, {}", SIMPLE_NAME, lat, lon);
        return location;
    }

    @Override
    public Optional<Location> findById(final long id) {
        final Optional<Location> location = locationRepository.findById(id);
        log.info("Get Optional<{}> by id - {}", SIMPLE_NAME, id);
        return location;
    }

    @Override
    public Location findByIdOrElseThrow(final long id) {
        return findById(id).orElseThrow(() -> new NotFoundException(SIMPLE_NAME, id));
    }

    @Override
    public void existsByIdOrElseThrow(final long id) {
        if (!locationRepository.existsById(id)) {
            throw new NotFoundException(SIMPLE_NAME, id);
        }
    }

    @Override
    @Transactional
    public void deleteById(final long id) {
        locationRepository.deleteById(id);
        log.info("Delete {} by id - {}", SIMPLE_NAME, id);
    }

    @Override
    public List<Location> findAllByNameContainingIgnoreCase(final String text, final int from, final int size) {
        final List<Location> locations = locationRepository
                .findAllByNameContainingIgnoreCase(text, PageRequest.of(from, size));
        log.info("Getting {} text - {} from - {} size - {}", SIMPLE_NAME, text, from, size);
        return locations;
    }

    @Override
    public Optional<Location> getByCoordinates(double lat, double lon) {
        return locationRepository.findByLatAndLon(lat, lon);
    }
}
