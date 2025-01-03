package ru.yandex.practicum.category.compilation.service;

import ru.yandex.practicum.category.compilation.model.dto.CompilationDto;
import ru.yandex.practicum.category.compilation.model.dto.CreateCompilationDto;
import ru.yandex.practicum.category.compilation.model.dto.UpdateCompilationDto;

import java.util.List;

public interface CompilationService {
    CompilationDto create(final CreateCompilationDto createCompilationDto);

    CompilationDto update(final UpdateCompilationDto updateCompilationDto, final long compId);

    void delete(final long compId);

    List<CompilationDto> getAll(Boolean pinned, final int from, final int size);

    CompilationDto getById(final long compId);
}
