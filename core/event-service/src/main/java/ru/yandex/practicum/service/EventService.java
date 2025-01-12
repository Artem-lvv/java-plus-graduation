package ru.yandex.practicum.service;


import jakarta.servlet.http.HttpServletRequest;
import ru.yandex.practicum.event.model.AdminParameter;
import ru.yandex.practicum.event.model.PublicParameter;
import ru.yandex.practicum.event.model.dto.CreateEventDto;
import ru.yandex.practicum.event.model.dto.EventDto;
import ru.yandex.practicum.event.model.dto.EventDtoWithObjects;
import ru.yandex.practicum.event.model.dto.UpdateEventDto;

import java.util.List;

public interface EventService {
    List<EventDto> getAllByAdmin(final AdminParameter adminParameter);

    EventDto updateByAdmin(final long eventId, final UpdateEventDto updateEventDto);

    EventDtoWithObjects create(final CreateEventDto createEventDto, final long userId);

    List<EventDto> getAllByUserId(final long userId, final int from, final int size);

    EventDto getByIdAndUserId(final long eventId, final long userId);

    EventDto getById(final long eventId, final HttpServletRequest request);

    List<EventDto> getAll(final PublicParameter publicParameter, final HttpServletRequest request);

    List<EventDto> getAllByLocation(final double lat, final double lon, final double radius);

    EventDtoWithObjects updateByUser(final long userId, final long eventId, final UpdateEventDto updateEventDto);
}
