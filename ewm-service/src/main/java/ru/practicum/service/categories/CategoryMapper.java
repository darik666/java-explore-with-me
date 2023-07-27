package ru.practicum.service.categories;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.model.Category;

import java.time.LocalDateTime;

/**
 * Маппер категорий
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryMapper {

    public static CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }

    public static Category toCategoryfromNew(NewCategoryDto dto) {
        Category cat = new Category();
        cat.setName(dto.getName());
        cat.setCreated(LocalDateTime.now());
        return cat;
    }

    public static Category toCategoryUpdate(CategoryDto dto, Long catId) {
        Category cat = new Category();
        cat.setId(catId);
        cat.setName(dto.getName());
        cat.setCreated(LocalDateTime.now());
        return cat;
    }
}