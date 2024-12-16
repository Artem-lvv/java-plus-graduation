package com.github.artemlv.ewm.compilation.converter;

import com.github.artemlv.ewm.compilation.model.Compilation;
import com.github.artemlv.ewm.compilation.model.dto.CompilationDto;
import com.github.artemlv.ewm.event.converter.EventToEventDtoConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CompilationToCompilationDtoConverter implements Converter<Compilation, CompilationDto> {
    private final EventToEventDtoConverter eventToEventDtoConverter;

    @Override
    public CompilationDto convert(final Compilation source) {
        return CompilationDto.builder()
                .id(source.getId())
                .title(source.getTitle())
                .pinned(source.isPinned())
                .events(ObjectUtils.isEmpty(source.getEvents()) ? List.of() : source.getEvents().stream()
                        .map(eventToEventDtoConverter::convert)
                        .toList())
                .build();
    }
}
