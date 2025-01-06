package ru.yandex.practicum.location.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.location.model.Location;
import ru.yandex.practicum.location.model.dto.LocationDto;

@Component
public class LocationToLocationDtoConverter implements Converter<Location, LocationDto> {
    @Override
    public LocationDto convert(final Location source) {
        return LocationDto.builder()
                .id(source.getId())
                .lon(source.getLon())
                .lat(source.getLat())
                .radius(source.getRadius())
                .name(source.getName())
                .build();
    }
}
