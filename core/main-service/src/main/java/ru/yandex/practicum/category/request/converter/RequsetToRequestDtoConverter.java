package ru.yandex.practicum.category.request.converter;

import ru.yandex.practicum.category.request.model.Request;
import ru.yandex.practicum.category.request.model.dto.RequestDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class RequsetToRequestDtoConverter implements Converter<Request, RequestDto> {
    @Override
    public RequestDto convert(final Request source) {
        return RequestDto.builder()
                .id(source.getId())
                .requester(source.getRequester().getId())
                .event(source.getEvent().getId())
                .status(source.getStatus())
                .created(source.getCreated())
                .build();
    }
}
