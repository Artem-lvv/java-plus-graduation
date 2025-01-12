package ru.yandex.practicum.request.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.request.model.Request;
import ru.yandex.practicum.request.model.dto.RequestDto;

@Component
public class RequsetToRequestDtoConverter implements Converter<Request, RequestDto> {
    @Override
    public RequestDto convert(final Request source) {
        return RequestDto.builder()
                .id(source.getId())
                .requester(source.getRequester())
                .event(source.getEvent())
                .status(source.getStatus())
                .created(source.getCreated())
                .build();
    }
}
