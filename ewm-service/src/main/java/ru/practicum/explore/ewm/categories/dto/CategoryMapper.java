package ru.practicum.explore.ewm.categories.dto;

import org.springframework.stereotype.Component;
import ru.practicum.explore.ewm.categories.Category;

@Component
public class CategoryMapper {
    public Category fromDtoNewToCategory(CategoryDtoNew dto) {
        return new Category(null, dto.getName());
    }

    public CategoryDto fromCategoryToDto(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }
}
