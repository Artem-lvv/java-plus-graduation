package ru.yandex.practicum.category.event.converter;

import ru.yandex.practicum.category.event.model.Event;
import ru.yandex.practicum.category.event.model.dto.CreateEventDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class CreateEventDtoToEventConverter implements Converter<CreateEventDto, Event> {
    @Override
    public Event convert(final CreateEventDto source) {
        return Event.builder()
                .annotation(source.annotation())
                .description(source.description())
                .eventDate(source.eventDate())
                .paid(source.paid())
                .participantLimit(source.participantLimit())
                .requestModeration(ObjectUtils.isEmpty(source.requestModeration()) || source.requestModeration())
                .title(source.title())
                .build();
    }
}
