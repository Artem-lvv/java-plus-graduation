package ru.yandex.practicum.request.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.request.model.Request;
import ru.yandex.practicum.request.model.dto.CreateRequestDto;

@Component
public class CreateRequestDtoToRequestConverter implements Converter<CreateRequestDto, Request> {
    @Override
    public Request convert(final CreateRequestDto source) {
        return Request.builder()
                .created(source.created())
                .status(source.status())
                .build();
    }
}
