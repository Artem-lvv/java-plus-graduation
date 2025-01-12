package ru.yandex.practicum.service;

import jakarta.validation.constraints.Positive;
import ru.yandex.practicum.request.model.dto.RequestDto;
import ru.yandex.practicum.request.model.dto.RequestStatusUpdateResultDto;
import ru.yandex.practicum.request.model.dto.UpdateRequestByIdsDto;

import java.util.List;

public interface RequestService {
    RequestDto create(final long userId, final long eventId);

    List<RequestDto> getAll(final long userId);

    RequestDto cancel(final long userId, final long requestId);

    List<RequestDto> getRequestsByUserIdAndEventId(long userId, long eventId);

    RequestStatusUpdateResultDto updateRequestsStatusByUserIdAndEventId(long userId, long eventId,
                                                                        UpdateRequestByIdsDto updateRequestByIdsDto);

    List<RequestDto> getRequestsByEventId(long eventId);
}
