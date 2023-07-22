package ru.practicum.service.categories;

import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.NewCategoryDto;
import ru.practicum.model.Category;

import java.time.LocalDateTime;

public class CategoryMapper {
    public static CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }

    public static Category toCategory(NewCategoryDto dto) {
        Category cat = new Category();
        cat.setName(dto.getName());
        cat.setCreated(LocalDateTime.now());
        return cat;
    }
}
