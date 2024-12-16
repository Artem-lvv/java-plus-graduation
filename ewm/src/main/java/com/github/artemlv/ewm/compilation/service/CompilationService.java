package com.github.artemlv.ewm.compilation.service;

import com.github.artemlv.ewm.compilation.model.dto.CompilationDto;
import com.github.artemlv.ewm.compilation.model.dto.CreateCompilationDto;
import com.github.artemlv.ewm.compilation.model.dto.UpdateCompilationDto;

import java.util.List;

public interface CompilationService {
    CompilationDto create(final CreateCompilationDto createCompilationDto);

    CompilationDto update(final UpdateCompilationDto updateCompilationDto, final long compId);

    void delete(final long compId);

    List<CompilationDto> getAll(Boolean pinned, final int from, final int size);

    CompilationDto getById(final long compId);
}
