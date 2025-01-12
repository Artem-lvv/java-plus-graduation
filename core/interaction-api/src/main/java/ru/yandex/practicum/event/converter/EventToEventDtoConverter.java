package ru.yandex.practicum.event.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.category.converter.CategoryToCategoryDto;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.event.model.dto.EventDto;
import ru.yandex.practicum.location.converter.LocationToLocationDtoConverter;
import ru.yandex.practicum.user.converter.UserToUserWithoutEmailDtoConverter;

@Component
@RequiredArgsConstructor
public class EventToEventDtoConverter implements Converter<Event, EventDto> {
    private final UserToUserWithoutEmailDtoConverter userWithoutEmailDtoConverter;
    private final LocationToLocationDtoConverter locationDtoConverter;
    private final CategoryToCategoryDto categoryDtoConverter;

    @Override
    public EventDto convert(final Event source) {
        return EventDto.builder()
                .id(source.getId())
                .annotation(source.getAnnotation())
                .category(source.getCategory())
                .confirmedRequests(source.getConfirmedRequests())
                .createdOn(source.getCreatedOn())
                .description(source.getDescription())
                .eventDate(source.getEventDate())
                .initiator(source.getInitiator())
                .location(source.getLocation())
                .paid(source.isPaid())
                .participantLimit(source.getParticipantLimit())
                .publishedOn(source.getPublishedOn())
                .requestModeration(source.isRequestModeration())
                .state(source.getState())
                .title(source.getTitle())
                .views(source.getViews())
                .build();
    }
}
