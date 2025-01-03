package ru.yandex.practicum.category.location.converter;

import ru.yandex.practicum.category.location.model.Location;
import ru.yandex.practicum.category.location.model.dto.CreateLocationDto;
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
