package ru.yandex.practicum.service;

import ru.yandex.practicum.location.model.Location;
import ru.yandex.practicum.location.model.dto.CreateLocationDto;
import ru.yandex.practicum.location.model.dto.LocationDto;
import ru.yandex.practicum.location.model.dto.LocationLatAndLonDto;
import ru.yandex.practicum.location.model.dto.UpdateLocationDto;
import ru.yandex.practicum.storage.LocationStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {
    @Qualifier("mvcConversionService")
    private final ConversionService cs;
    private final LocationStorage locationStorage;

    @Override
    public LocationDto create(CreateLocationDto createLocationDto) {
        Location location = cs.convert(createLocationDto, Location.class);
        return cs.convert(locationStorage.save(location), LocationDto.class);
    }

    @Override
    public LocationDto getByCoordinatesOrElseCreate(final LocationLatAndLonDto locationLatAndLonDto) {
        return cs.convert(locationStorage.findByLatAndLon(locationLatAndLonDto.lat(), locationLatAndLonDto.lon())
                        .orElseGet(() -> locationStorage.save(cs.convert(locationLatAndLonDto, Location.class))
                        ),
                LocationDto.class
        );
    }

    @Override
    public LocationDto getById(final long id) {
        return cs.convert(locationStorage.findByIdOrElseThrow(id), LocationDto.class);
    }

    @Override
    public LocationDto updateById(final long id, final UpdateLocationDto updateLocationDto) {
        Location location = locationStorage.findByIdOrElseThrow(id);

        if (updateLocationDto.lat() != 0) {
            location.setLat(updateLocationDto.lat());
        }

        if (updateLocationDto.lon() != 0) {
            location.setLon(updateLocationDto.lon());
        }

        if (!ObjectUtils.isEmpty(updateLocationDto.name())) {
            location.setName(updateLocationDto.name());
        }

        if (updateLocationDto.radius() > 0) {
            location.setRadius(updateLocationDto.radius());
        }

        return cs.convert(locationStorage.save(location), LocationDto.class);
    }

    @Override
    public void deleteById(final long id) {
        locationStorage.existsByIdOrElseThrow(id);
        locationStorage.deleteById(id);
    }

    @Override
    public List<LocationDto> getAll(final String text, final int from, final int size) {
        return locationStorage.findAllByNameContainingIgnoreCase(text, from, size).stream()
                .map(location -> cs.convert(location, LocationDto.class))
                .toList();
    }

    @Override
    public LocationDto getByCoordinates(double lat, double lon) {
        Optional<Location> byCoordinates = locationStorage.getByCoordinates(lat, lon);

        if (byCoordinates.isPresent()) {
            return cs.convert(byCoordinates.get(), LocationDto.class);
        } else {}
        return null;
    }
}
