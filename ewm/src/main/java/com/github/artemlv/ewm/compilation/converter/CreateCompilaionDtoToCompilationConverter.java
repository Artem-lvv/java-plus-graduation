package com.github.artemlv.ewm.compilation.converter;

import com.github.artemlv.ewm.compilation.model.Compilation;
import com.github.artemlv.ewm.compilation.model.dto.CreateCompilationDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CreateCompilaionDtoToCompilationConverter implements Converter<CreateCompilationDto, Compilation> {
    @Override
    public Compilation convert(final CreateCompilationDto source) {
        return Compilation.builder()
                .title(source.title())
                .pinned(source.pinned())
                .build();
    }
}
