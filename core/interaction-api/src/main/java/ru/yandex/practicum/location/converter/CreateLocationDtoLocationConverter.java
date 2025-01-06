package ru.yandex.practicum.location.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.location.model.Location;
import ru.yandex.practicum.location.model.dto.CreateLocationDto;

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
