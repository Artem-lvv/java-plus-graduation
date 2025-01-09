package ru.yandex.practicum.event.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.category.converter.CategoryDtoToCategoryConverter;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.event.model.dto.EventDto;
import ru.yandex.practicum.location.converter.LocationDtoToLocationConverter;
import ru.yandex.practicum.user.converter.UserDtoToUserWithoutEmailConverter;

@Component
@RequiredArgsConstructor
public class EventDtoToEventConverter implements Converter<EventDto, Event> {
    private final LocationDtoToLocationConverter locationDtoToLocationConverter;
    private final CategoryDtoToCategoryConverter categoryDtoToCategoryConverter;
    private final UserDtoToUserWithoutEmailConverter userDtoToUserWithoutEmailConverter;

    @Override
    public Event convert(final EventDto source) {
        return Event.builder()
                .id(source.id())
                .annotation(source.annotation())
                .category(categoryDtoToCategoryConverter.convert(source.category()))
                .confirmedRequests(source.confirmedRequests())
                .createdOn(source.createdOn())
                .description(source.description())
                .eventDate(source.eventDate())
                .initiator(userDtoToUserWithoutEmailConverter.convert(source.initiator()))
                .location(locationDtoToLocationConverter.convert(source.location()))
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
