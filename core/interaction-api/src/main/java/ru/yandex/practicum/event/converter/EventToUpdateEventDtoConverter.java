package ru.yandex.practicum.event.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.event.model.dto.UpdateEventDto;

@Component
@RequiredArgsConstructor
public class EventToUpdateEventDtoConverter implements Converter<Event, UpdateEventDto> {
    @Override
    public UpdateEventDto convert(final Event source) {
        return new UpdateEventDto(source.getAnnotation(),
                source.getCategory(),
                source.getDescription(),
                source.getEventDate(),
                null,
                source.isPaid(),
                source.getParticipantLimit(),
                source.isRequestModeration(),
                null,
                source.getTitle());

    }
}
