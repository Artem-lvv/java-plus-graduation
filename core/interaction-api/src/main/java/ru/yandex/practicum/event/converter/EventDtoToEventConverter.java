package ru.yandex.practicum.event.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.event.model.dto.EventDto;

@Component
@RequiredArgsConstructor
public class EventDtoToEventConverter implements Converter<EventDto, Event> {

    @Override
    public Event convert(final EventDto source) {
        return Event.builder()
                .id(source.id())
                .annotation(source.annotation())
                .category(source.category())
                .confirmedRequests(source.confirmedRequests())
                .createdOn(source.createdOn())
                .description(source.description())
                .eventDate(source.eventDate())
                .initiator(source.initiator())
                .location(source.location())
                .paid(source.paid())
                .participantLimit(source.participantLimit())
                .publishedOn(source.publishedOn())
                .requestModeration(source.requestModeration())
                .state(source.state())
                .title(source.title())
                .rating(source.rating())
                .build();
    }
}
