package ru.yandex.practicum.category.event.converter;

import ru.yandex.practicum.category.event.model.Event;
import ru.yandex.practicum.category.event.model.dto.UpdateEventDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

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
