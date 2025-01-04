package ru.yandex.practicum.category.location.converter;

import ru.yandex.practicum.category.location.model.dto.LocationDto;
import ru.yandex.practicum.category.location.model.dto.LocationLatAndLonDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class LocationDtoToLocationLatAndLonDtoConverter implements Converter<LocationDto, LocationLatAndLonDto> {
    @Override
    public LocationLatAndLonDto convert(final LocationDto source) {
        return LocationLatAndLonDto.builder()
                .lon(source.lon())
                .lat(source.lat())
                .build();
    }
}
