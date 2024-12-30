package ru.yandex.practicum.category.compilation.converter;

import ru.yandex.practicum.category.compilation.model.Compilation;
import ru.yandex.practicum.category.compilation.model.dto.CompilationDto;
import ru.yandex.practicum.category.event.converter.EventToEventDtoConverter;
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
