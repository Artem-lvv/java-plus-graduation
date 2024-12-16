package com.github.artemlv.ewm.event.service;

import com.github.artemlv.ewm.event.model.AdminParameter;
import com.github.artemlv.ewm.event.model.PublicParameter;
import com.github.artemlv.ewm.event.model.dto.CreateEventDto;
import com.github.artemlv.ewm.event.model.dto.EventDto;
import com.github.artemlv.ewm.event.model.dto.UpdateEventDto;
import com.github.artemlv.ewm.request.model.dto.RequestDto;
import com.github.artemlv.ewm.request.model.dto.RequestStatusUpdateResultDto;
import com.github.artemlv.ewm.request.model.dto.UpdateRequestByIdsDto;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface EventService {
    List<EventDto> getAllByAdmin(final AdminParameter adminParameter);

    EventDto updateByAdmin(final long eventId, final UpdateEventDto updateEventDto);

    EventDto create(final CreateEventDto createEventDto, final long userId);

    List<EventDto> getAllByUserId(final long userId, final int from, final int size);

    List<RequestDto> getRequestsByUserIdAndEventId(final long userId, final long eventId);

    EventDto getByIdAndUserId(final long eventId, final long userId);

    RequestStatusUpdateResultDto updateRequestsStatusByUserIdAndEventId(final long userId,
                                                                        final long eventId,
                                                                        final UpdateRequestByIdsDto update);

    EventDto getById(final long eventId, final HttpServletRequest request);

    List<EventDto> getAll(final PublicParameter publicParameter, final HttpServletRequest request);

    List<EventDto> getAllByLocation(final double lat, final double lon, final double radius);

    EventDto updateByUser(final long userId, final long eventId, final UpdateEventDto updateEventDto);
}
