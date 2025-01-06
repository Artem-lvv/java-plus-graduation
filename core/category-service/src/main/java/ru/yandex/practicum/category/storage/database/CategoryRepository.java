package ru.yandex.practicum.category.storage.database;

import ru.yandex.practicum.category.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}