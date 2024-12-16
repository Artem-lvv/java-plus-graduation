package com.github.artemlv.ewm.location.converter;

import com.github.artemlv.ewm.location.model.Location;
import com.github.artemlv.ewm.location.model.dto.CreateLocationDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CreateLocationDtoLocationConverter implements Converter<CreateLocationDto, Location> {
    @Override
    public Location convert(final CreateLocationDto source) {
        return Location.builder()
                .name(source.name())
                .lon(source.lon())
                .lat(source.lat())
                .radius(source.radius())
                .build();
    }
}
