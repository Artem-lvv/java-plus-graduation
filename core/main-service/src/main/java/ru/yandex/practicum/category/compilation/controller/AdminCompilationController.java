package ru.yandex.practicum.category.compilation.controller;

import ru.yandex.practicum.category.compilation.model.Compilation;
import ru.yandex.practicum.category.compilation.model.dto.CompilationDto;
import ru.yandex.practicum.category.compilation.model.dto.CreateCompilationDto;
import ru.yandex.practicum.category.compilation.model.dto.UpdateCompilationDto;
import ru.yandex.practicum.category.compilation.service.CompilationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
public class AdminCompilationController {
    private static final String SIMPLE_NAME = Compilation.class.getSimpleName();
    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto create(@RequestBody @Valid final CreateCompilationDto createCompilationDto) {
        log.info("Request to create {} - {}", SIMPLE_NAME, createCompilationDto);
        return compilationService.create(createCompilationDto);
    }

    @PatchMapping("/{compId}")
    public CompilationDto update(@RequestBody @Valid final UpdateCompilationDto updateCompilationDto,
                                 @PathVariable @Positive final long compId) {
        log.info("Request to change the {} by id - {} - {}", SIMPLE_NAME, compId, updateCompilationDto);
        return compilationService.update(updateCompilationDto, compId);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable @Positive final long compId) {
        log.info("Request to delete {} by id - {}", SIMPLE_NAME, compId);
        compilationService.delete(compId);
    }
}
