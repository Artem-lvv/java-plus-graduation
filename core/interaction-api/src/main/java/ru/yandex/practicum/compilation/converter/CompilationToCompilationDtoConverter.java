package ru.yandex.practicum.compilation.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.compilation.model.Compilation;
import ru.yandex.practicum.compilation.model.dto.CompilationDto;

@Component
@RequiredArgsConstructor
public class CompilationToCompilationDtoConverter implements Converter<Compilation, CompilationDto> {

    @Override
    public CompilationDto convert(final Compilation source) {
        return CompilationDto.builder()
                .id(source.getId())
                .title(source.getTitle())
                .pinned(source.isPinned())
                .build();
    }
}
