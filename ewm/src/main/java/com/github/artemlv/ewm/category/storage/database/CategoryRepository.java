package com.github.artemlv.ewm.category.storage.database;

import com.github.artemlv.ewm.category.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}