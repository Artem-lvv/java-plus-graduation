package com.github.artemlv.ewm.event.converter;

import com.github.artemlv.ewm.event.model.Event;
import com.github.artemlv.ewm.event.model.dto.CreateEventDto;
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
