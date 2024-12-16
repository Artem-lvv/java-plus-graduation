package com.github.artemlv.ewm.category.service;

import com.github.artemlv.ewm.category.model.dto.CategoryDto;
import com.github.artemlv.ewm.category.model.dto.CreateCategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto create(final CreateCategoryDto createCategoryDto);

    CategoryDto update(final CreateCategoryDto createCategoryDto, final long id);

    void deleteById(final long id);

    List<CategoryDto> getAll(final int from, final int size);

    CategoryDto getById(final long id);
}
