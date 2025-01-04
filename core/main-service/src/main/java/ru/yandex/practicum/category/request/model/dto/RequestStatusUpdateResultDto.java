package ru.yandex.practicum.category.request.model.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record RequestStatusUpdateResultDto(
        List<RequestDto> confirmedRequests,
        List<RequestDto> rejectedRequests
) {
}
