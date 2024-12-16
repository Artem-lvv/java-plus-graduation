package com.github.artemlv.ewm.compilation.converter;

import com.github.artemlv.ewm.compilation.model.Compilation;
import com.github.artemlv.ewm.compilation.model.dto.UpdateCompilationDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UpdateCompilaionDtoToCompilationConverter implements Converter<UpdateCompilationDto, Compilation> {
    @Override
    public Compilation convert(final UpdateCompilationDto source) {
        return Compilation.builder()
                .title(source.title())
                .build();
    }
}
