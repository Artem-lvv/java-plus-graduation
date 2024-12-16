package com.github.artemlv.ewm.category.storage;

import com.github.artemlv.ewm.category.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryStorage {
    Category save(Category category);

    void deleteById(final long id);

    List<Category> getAll(final int from, final int size);

    Optional<Category> getById(final long id);

    Category getByIdOrElseThrow(final long id);

    void existsByIdOrElseThrow(final long id);
}
