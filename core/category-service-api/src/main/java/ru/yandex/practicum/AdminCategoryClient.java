package ru.yandex.practicum;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import ru.yandex.practicum.category.model.dto.CategoryDto;
import ru.yandex.practicum.category.model.dto.CreateCategoryDto;

@FeignClient(name = "${admin.category.service.name:CATEGORY-SERVICE}", url = "${admin.category.service.url}")
public interface AdminCategoryClient {

    @PostMapping("/admin/categories")
    CategoryDto create(@RequestBody @Valid CreateCategoryDto createCategoryDto);

    @PatchMapping("/admin/categories/{catId}")
    CategoryDto update(@PathVariable @Positive long catId,
                       @RequestBody @Valid CreateCategoryDto createCategoryDto);

    @DeleteMapping("/admin/categories/{catId}")
    void delete(@PathVariable @Positive long catId);
}
