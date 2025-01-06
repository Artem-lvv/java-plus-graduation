package ru.yandex.practicum.event.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.event.model.dto.CreateEventDto;

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
