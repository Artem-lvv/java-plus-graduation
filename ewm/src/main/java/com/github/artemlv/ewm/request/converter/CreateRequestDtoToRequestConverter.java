package com.github.artemlv.ewm.request.converter;

import com.github.artemlv.ewm.request.model.Request;
import com.github.artemlv.ewm.request.model.dto.CreateRequestDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CreateRequestDtoToRequestConverter implements Converter<CreateRequestDto, Request> {
    @Override
    public Request convert(final CreateRequestDto source) {
        return Request.builder()
                .created(source.created())
                .status(source.status())
                .build();
    }
}
