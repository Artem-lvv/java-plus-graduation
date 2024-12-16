package com.github.artemlv.ewm.event.converter;

import com.github.artemlv.ewm.category.converter.CategoryToCategoryDto;
import com.github.artemlv.ewm.event.model.Event;
import com.github.artemlv.ewm.event.model.dto.EventDto;
import com.github.artemlv.ewm.location.converter.LocationToLocationDtoConverter;
import com.github.artemlv.ewm.user.converter.UserToUserWithoutEmailDtoConverter;
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
