package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import ru.yandex.practicum.AdminEventClient;
import ru.yandex.practicum.compilation.model.Compilation;
import ru.yandex.practicum.compilation.model.dto.CompilationDto;
import ru.yandex.practicum.compilation.model.dto.CreateCompilationDto;
import ru.yandex.practicum.compilation.model.dto.UpdateCompilationDto;
import ru.yandex.practicum.event.model.dto.EventDtoWithObjects;
import ru.yandex.practicum.exception.type.NotFoundException;
import ru.yandex.practicum.storage.CompilationStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    @Qualifier("mvcConversionService")
    private final ConversionService cs;
    private final CompilationStorage compilationStorage;
    private final AdminEventClient adminEventClient;

    @Override
    public CompilationDto create(final CreateCompilationDto createCompilationDto) {
        Compilation compilation = cs.convert(createCompilationDto, Compilation.class);

        List<EventDtoWithObjects> eventDtos = new ArrayList<>();

        if (!ObjectUtils.isEmpty(createCompilationDto.events())) {
            eventDtos = adminEventClient.getAll(null,
                    null,
                    null,
                    createCompilationDto.events().stream().toList(),
                    null,
                    null,
                    0,
                    10);

            if (eventDtos.size() != createCompilationDto.events().size()) {
                throw new NotFoundException("the number of events found does not correspond to the requirements");
            }

            compilation.setEvents(createCompilationDto.events().stream().toList());
        }

        compilationStorage.save(compilation);

        return CompilationDto.builder()
                .id(compilation.getId())
                .pinned(compilation.isPinned())
                .title(compilation.getTitle())
                .events(eventDtos)
                .build();
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

        List<EventDtoWithObjects> eventDtos = new ArrayList<>();

        if (!ObjectUtils.isEmpty(updateCompilationDto.events())) {
            eventDtos = adminEventClient.getAll(null,
                    null,
                    null,
                    updateCompilationDto.events().stream().toList(),
                    null,
                    null,
                    0,
                    Integer.MAX_VALUE);

            compilationInStorage.setEvents(new ArrayList<>(updateCompilationDto.events()));
        }

        log.info("Update compilation - {}", compilationInStorage);
        compilationStorage.save(compilationInStorage);

        return CompilationDto.builder()
                .id(compilationInStorage.getId())
                .pinned(compilationInStorage.isPinned())
                .title(compilationInStorage.getTitle())
                .events(Objects.isNull(eventDtos) ? new ArrayList<>() : eventDtos)
                .build();
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

        List<CompilationDto> compilationDtos = new ArrayList<>();

        for (Compilation compilation : compilations) {
            List<EventDtoWithObjects> eventDtos = new ArrayList<>();

            if (!compilation.getEvents().isEmpty()) {
                eventDtos = adminEventClient.getAll(null,
                        null,
                        null,
                        compilation.getEvents(),
                        null,
                        null,
                        0,
                        Integer.MAX_VALUE);
            }

            compilationDtos.add(CompilationDto.builder()
                            .id(compilation.getId())
                            .title(compilation.getTitle())
                            .pinned(compilation.isPinned())
                            .events(Objects.isNull(eventDtos) ? new ArrayList<>() : eventDtos)
                    .build());
        }

        return compilationDtos;
    }

    @Override
    public CompilationDto getById(final long compId) {
        Compilation compilation = compilationStorage.getByIdOrElseThrow(compId);

        List<EventDtoWithObjects> eventDtos = new ArrayList<>();

        if (!ObjectUtils.isEmpty(compilation.getEvents())) {
            eventDtos = adminEventClient.getAll(null,
                    null,
                    null,
                    compilation.getEvents(),
                    null,
                    null,
                    0,
                    Integer.MAX_VALUE);
        }

        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.isPinned())
                .events(eventDtos)
                .build();
    }
}
