package com.github.artemlv.ewm.compilation.service;


import com.github.artemlv.ewm.compilation.model.Compilation;
import com.github.artemlv.ewm.compilation.model.dto.CompilationDto;
import com.github.artemlv.ewm.compilation.model.dto.CreateCompilationDto;
import com.github.artemlv.ewm.compilation.model.dto.UpdateCompilationDto;
import com.github.artemlv.ewm.compilation.storage.CompilationStorage;
import com.github.artemlv.ewm.event.model.Event;
import com.github.artemlv.ewm.event.storage.EventStorage;
import com.github.artemlv.ewm.exception.type.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    @Qualifier("mvcConversionService")
    private final ConversionService cs;
    private final CompilationStorage compilationStorage;
    private final EventStorage eventStorage;

    @Override
    public CompilationDto create(final CreateCompilationDto createCompilationDto) {
        Compilation compilation = cs.convert(createCompilationDto, Compilation.class);

        if (!ObjectUtils.isEmpty(createCompilationDto.events())) {
            final List<Event> events = eventStorage.findAllById(createCompilationDto.events());

            if (events.size() != createCompilationDto.events().size()) {
                throw new NotFoundException("the number of events found does not correspond to the requirements");
            }

            compilation.setEvents(events);
        }

        return cs.convert(compilationStorage.save(compilation), CompilationDto.class);
    }

    @Override
    public CompilationDto update(final UpdateCompilationDto updateCompilationDto, final long compId) {
        Compilation compilationInStorage = compilationStorage.getByIdOrElseThrow(compId);

        if (ObjectUtils.isEmpty(updateCompilationDto.pinned())) {
            compilationInStorage.setPinned(compilationInStorage.isPinned());
        }

        if (ObjectUtils.isEmpty(updateCompilationDto.title())) {
            compilationInStorage.setTitle(compilationInStorage.getTitle());
        }

        if (!ObjectUtils.isEmpty(updateCompilationDto.events())) {
            final List<Event> events = eventStorage.findAllById(updateCompilationDto.events());
            compilationInStorage.setEvents(events);
        }

        log.info("Update compilation - {}", compilationInStorage);

        return cs.convert(compilationStorage.save(compilationInStorage), CompilationDto.class);
    }

    @Override
    public void delete(final long compId) {
        compilationStorage.existsByIdOrElseThrow(compId);
        compilationStorage.deleteById(compId);
    }

    @Override
    public List<CompilationDto> getAll(final Boolean pinned, final int from, final int size) {
        PageRequest page = PageRequest.of(from / size, size);

        List<Compilation> compilations = ObjectUtils.isEmpty(pinned) ? compilationStorage.findAll(page)
                : compilationStorage.findAllByPinnedIs(pinned, page);

        return compilations.stream()
                .map(compilation -> cs.convert(compilation, CompilationDto.class))
                .toList();
    }

    @Override
    public CompilationDto getById(final long compId) {
        return cs.convert(compilationStorage.getByIdOrElseThrow(compId), CompilationDto.class);
    }
}
