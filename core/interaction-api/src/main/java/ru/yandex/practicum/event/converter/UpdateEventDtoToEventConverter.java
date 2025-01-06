package ru.yandex.practicum.event.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.event.model.dto.UpdateEventDto;

@Component
public class UpdateEventDtoToEventConverter implements Converter<UpdateEventDto, Event> {
    @Override
    public Event convert(final UpdateEventDto source) {
        return Event.builder()
                .annotation(source.annotation())
                .description(source.description())
                .eventDate(source.eventDate())
                .requestModeration(source.requestModeration())
                .title(source.title())
                .build();
    }
}
