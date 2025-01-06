package ru.yandex.practicum.location.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.location.model.Location;
import ru.yandex.practicum.location.model.dto.LocationLatAndLonDto;

@Component
public class LocationLatAndLonDtoToLocationConverter implements Converter<LocationLatAndLonDto, Location> {
    @Override
    public Location convert(final LocationLatAndLonDto source) {
        return Location.builder()
                .lon(source.lon())
                .lat(source.lat())
                .build();
    }
}
