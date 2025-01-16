package ru.yandex.practicum.event.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.event.model.dto.EventDtoWithObjects;

@Component
@RequiredArgsConstructor
public class EventDtoWithObjToEventConverter implements Converter<EventDtoWithObjects, Event> {

    @Override
    public Event convert(final EventDtoWithObjects source) {
        return Event.builder()
                .id(source.id())
                .annotation(source.annotation())
                .category(source.category().id())
                .confirmedRequests(source.confirmedRequests())
                .createdOn(source.createdOn())
                .description(source.description())
                .eventDate(source.eventDate())
                .initiator(source.initiator().id())
                .location(source.location().id())
                .paid(source.paid())
                .participantLimit(source.participantLimit())
                .publishedOn(source.publishedOn())
                .requestModeration(source.requestModeration())
                .state(source.state())
                .title(source.title())
                .views(source.views())
                .build();
    }
}
