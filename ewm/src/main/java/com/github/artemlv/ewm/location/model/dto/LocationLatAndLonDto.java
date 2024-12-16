package com.github.artemlv.ewm.location.model.dto;

import lombok.Builder;

@Builder
public record LocationLatAndLonDto(
        double lat,
        double lon
) {
}
