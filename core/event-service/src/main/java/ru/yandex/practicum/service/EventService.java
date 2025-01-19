package ru.yandex.practicum.service;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import ru.yandex.practicum.event.model.AdminParameter;
import ru.yandex.practicum.event.model.PublicParameter;
import ru.yandex.practicum.event.model.dto.CreateEventDto;
import ru.yandex.practicum.event.model.dto.EventDtoWithObjects;
import ru.yandex.practicum.event.model.dto.UpdateEventDto;

import java.util.List;

public interface EventService {
    List<EventDtoWithObjects> getAllByAdmin(final AdminParameter adminParameter);

    EventDtoWithObjects updateByAdmin(final long eventId, final UpdateEventDto updateEventDto);

    EventDtoWithObjects create(final CreateEventDto createEventDto, final long userId);

    List<EventDtoWithObjects> getAllByUserId(final long userId, final int from, final int size);

    EventDtoWithObjects getByIdAndUserId(final long eventId, final long userId);

    EventDtoWithObjects getById(final long eventId, final HttpServletRequest request, long userId);

    List<EventDtoWithObjects> getAll(final PublicParameter publicParameter, final HttpServletRequest request);

    List<EventDtoWithObjects> getAllByLocation(final double lat, final double lon, final double radius);

    EventDtoWithObjects updateByUser(final long userId, final long eventId, final UpdateEventDto updateEventDto);

    List<EventDtoWithObjects> getRecommendations(long userId);

    void addLikeEvent(long eventId, long userId);
}
