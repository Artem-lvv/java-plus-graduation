package ru.yandex.practicum.location.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.location.model.Location;
import ru.yandex.practicum.location.model.dto.LocationDto;

@Component
public class LocationDtoToLocationConverter implements Converter<LocationDto, Location> {
    @Override
    public Location convert(final LocationDto source) {
        return Location.builder()
                .id(source.id())
                .lon(source.lon())
                .lat(source.lat())
                .radius(source.radius())
                .name(source.name())
                .build();
    }
}
