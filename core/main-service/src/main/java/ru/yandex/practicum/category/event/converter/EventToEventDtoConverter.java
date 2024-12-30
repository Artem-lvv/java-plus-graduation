package ru.yandex.practicum.category.event.converter;

import ru.yandex.practicum.category.converter.CategoryToCategoryDto;
import ru.yandex.practicum.category.event.model.Event;
import ru.yandex.practicum.category.event.model.dto.EventDto;
import ru.yandex.practicum.category.location.converter.LocationToLocationDtoConverter;
import ru.yandex.practicum.category.user.converter.UserToUserWithoutEmailDtoConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

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
                .category(categoryDtoConverter.convert(source.getCategory()))
                .confirmedRequests(source.getConfirmedRequests())
                .createdOn(source.getCreatedOn())
                .description(source.getDescription())
                .eventDate(source.getEventDate())
                .initiator(userWithoutEmailDtoConverter.convert(source.getInitiator()))
                .location(locationDtoConverter.convert(source.getLocation()))
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
