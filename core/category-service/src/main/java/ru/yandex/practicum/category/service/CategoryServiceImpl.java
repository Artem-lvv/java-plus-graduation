package ru.yandex.practicum.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.AdminEventClient;
import ru.yandex.practicum.category.model.Category;
import ru.yandex.practicum.category.model.dto.CategoryDto;
import ru.yandex.practicum.category.model.dto.CreateCategoryDto;
import ru.yandex.practicum.category.storage.CategoryStorage;
import ru.yandex.practicum.event.model.dto.EventDtoWithObjects;
import ru.yandex.practicum.exception.type.ConflictException;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    @Qualifier("mvcConversionService")
    private final ConversionService cs;
    private final CategoryStorage categoryStorage;
    private final AdminEventClient adminEventClient;

    @Override
    public CategoryDto create(CreateCategoryDto createCategoryDto) {
        Category category = cs.convert(createCategoryDto, Category.class);
        return cs.convert(categoryStorage.save(category), CategoryDto.class);
    }

    @Override
    public CategoryDto update(final CreateCategoryDto createCategoryDto, final long id) {
        Category category = categoryStorage.getByIdOrElseThrow(id);
        Optional<Category> byName = categoryStorage.findByName(createCategoryDto.name());

        if (byName.isPresent() && !byName.get().equals(category)) {
            throw new ConflictException(String.format("Category with name '%s' already exists",
                    createCategoryDto.name()));
        }

        category.setName(createCategoryDto.name());
        log.info("Update category - {}", category);

        return cs.convert(categoryStorage.save(category), CategoryDto.class);
    }

    @Override
    public void deleteById(final long id) {
        List<EventDtoWithObjects> eventDtos = adminEventClient.getAll(null,
                null,
                List.of(id),
                null,
                null,
                null,
                0,
                1);

        if (!eventDtos.isEmpty()) {
            throw new ConflictException("Category with id " + id + " exists in Event");
        }

        categoryStorage.existsByIdOrElseThrow(id);
        categoryStorage.deleteById(id);
    }

    @Override
    public List<CategoryDto> getAll(final int from, final int size) {
        return categoryStorage.getAll(from, size).stream()
                .map(category -> cs.convert(category, CategoryDto.class))
                .toList();
    }

    @Override
    public CategoryDto getById(final long id) {
        return cs.convert(categoryStorage.getByIdOrElseThrow(id), CategoryDto.class);
    }
}
