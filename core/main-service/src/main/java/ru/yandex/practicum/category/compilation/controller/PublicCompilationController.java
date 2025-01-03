package ru.yandex.practicum.category.compilation.controller;

import ru.yandex.practicum.category.compilation.model.Compilation;
import ru.yandex.practicum.category.compilation.model.dto.CompilationDto;
import ru.yandex.practicum.category.compilation.service.CompilationService;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
public class PublicCompilationController {
    private static final String SIMPLE_NAME = Compilation.class.getSimpleName();
    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> getCompilations(@RequestParam(required = false) final Boolean pinned,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero final int from,
                                                @RequestParam(defaultValue = "10") @Positive final int size) {
        log.info("Request for {} - {} beginning - {} size - {}", SIMPLE_NAME, pinned, from, size);
        return compilationService.getAll(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilation(@PathVariable @Positive final long compId) {
        log.info("Get {} - by id {}", SIMPLE_NAME, compId);
        return compilationService.getById(compId);
    }

}
