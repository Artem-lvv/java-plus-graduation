package ru.yandex.practicum.event.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.category.converter.CategoryToCategoryDto;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.event.model.dto.EventDto;
import ru.yandex.practicum.event.model.dto.UpdateEventDto;
import ru.yandex.practicum.location.converter.LocationToLocationDtoConverter;
import ru.yandex.practicum.location.model.dto.LocationLatAndLonDto;
import ru.yandex.practicum.state.StateAction;
import ru.yandex.practicum.user.converter.UserToUserWithoutEmailDtoConverter;

@Component
@RequiredArgsConstructor
public class EventToUpdateEventDtoConverter implements Converter<Event, UpdateEventDto> {
    @Override
    public UpdateEventDto convert(final Event source) {
        return new UpdateEventDto(source.getAnnotation(),
                source.getCategory().getId(),
                source.getDescription(),
                source.getEventDate(),
                LocationLatAndLonDto.builder()
                        .lat(source.getLocation().getLat())
                        .lon(source.getLocation().getLon())
                        .build(),
                source.isPaid(),
                source.getParticipantLimit(),
                source.isRequestModeration(),
//                StateAction.valueOf(source.getState().name()),
//                switch (source.getState()) {
//                    case PENDING -> null;
//                    case PUBLISHED -> StateAction.PUBLISH_EVENT;
//                    case CANCELED -> StateAction.CANCEL_REVIEW;
//                    case CONFIRMED -> null;
//                    case REJECTED -> StateAction.REJECT_EVENT;
//                }
                null,

                source.getTitle());

    }
}
