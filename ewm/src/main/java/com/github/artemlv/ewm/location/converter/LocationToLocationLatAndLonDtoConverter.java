package com.github.artemlv.ewm.location.converter;

import com.github.artemlv.ewm.location.model.Location;
import com.github.artemlv.ewm.location.model.dto.LocationLatAndLonDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class LocationToLocationLatAndLonDtoConverter implements Converter<Location, LocationLatAndLonDto> {
    @Override
    public LocationLatAndLonDto convert(final Location source) {
        return LocationLatAndLonDto.builder()
                .lon(source.getLon())
                .lat(source.getLat())
                .build();
    }
}
