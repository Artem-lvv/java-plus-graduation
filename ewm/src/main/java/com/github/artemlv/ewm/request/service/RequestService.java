package com.github.artemlv.ewm.request.service;

import com.github.artemlv.ewm.request.model.dto.RequestDto;

import java.util.List;

public interface RequestService {
    RequestDto create(final long userId, final long eventId);

    List<RequestDto> getAll(final long userId);

    RequestDto cancel(final long userId, final long requestId);
}
