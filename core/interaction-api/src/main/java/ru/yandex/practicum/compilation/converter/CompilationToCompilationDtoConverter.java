package ru.yandex.practicum.compilation.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import ru.yandex.practicum.compilation.model.Compilation;
import ru.yandex.practicum.compilation.model.dto.CompilationDto;
import ru.yandex.practicum.event.converter.EventToEventDtoConverter;

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
