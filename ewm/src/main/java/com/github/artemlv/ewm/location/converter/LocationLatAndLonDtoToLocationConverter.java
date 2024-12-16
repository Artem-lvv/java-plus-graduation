package com.github.artemlv.ewm.location.converter;

import com.github.artemlv.ewm.location.model.Location;
import com.github.artemlv.ewm.location.model.dto.LocationLatAndLonDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

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
