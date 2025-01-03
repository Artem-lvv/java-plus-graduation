package ru.yandex.practicum.category.location.converter;

import ru.yandex.practicum.category.location.model.Location;
import ru.yandex.practicum.category.location.model.dto.LocationDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

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
