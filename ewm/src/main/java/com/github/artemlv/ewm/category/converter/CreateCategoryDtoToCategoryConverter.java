package com.github.artemlv.ewm.category.converter;

import com.github.artemlv.ewm.category.model.Category;
import com.github.artemlv.ewm.category.model.dto.CreateCategoryDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CreateCategoryDtoToCategoryConverter implements Converter<CreateCategoryDto, Category> {
    @Override
    public Category convert(final CreateCategoryDto source) {
        return Category.builder()
                .name(source.name())
                .build();
    }
}
