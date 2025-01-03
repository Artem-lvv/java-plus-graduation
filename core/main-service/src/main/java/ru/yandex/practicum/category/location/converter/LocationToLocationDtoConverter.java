package ru.yandex.practicum.category.location.converter;

import ru.yandex.practicum.category.location.model.Location;
import ru.yandex.practicum.category.location.model.dto.LocationDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

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
