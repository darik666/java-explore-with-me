package ru.practicum.service.categories;

import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.NewCategoryDto;

import java.util.List;

public interface CategoriesService {
    CategoryDto create(NewCategoryDto dto);

    CategoryDto update(CategoryDto dto);

    void delete(Long id);

    List<CategoryDto> getAllCategories(int from, int size);

    CategoryDto getById(long catId);
}
