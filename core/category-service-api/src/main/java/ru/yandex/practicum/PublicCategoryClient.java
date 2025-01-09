package ru.yandex.practicum;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import ru.yandex.practicum.category.model.dto.CategoryDto;

import java.util.List;

@FeignClient(name = "${public.category.service.name:CATEGORY-SERVICE}", url = "${public.category.service.url}")
public interface PublicCategoryClient {

    @GetMapping("/categories")
    List<CategoryDto> getAll(@RequestParam(defaultValue = "0") @PositiveOrZero int from,
                             @RequestParam(defaultValue = "10") @Positive int size);

    @GetMapping("/categories/{catId}")
    CategoryDto getById(@PathVariable @Positive long catId);
}
