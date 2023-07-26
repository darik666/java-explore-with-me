package ru.practicum.service.categories;

import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface CategoriesService {
    CategoryDto create(NewCategoryDto dto);

    CategoryDto update(Long catId, CategoryDto dto);

    void delete(Long id);

    List<CategoryDto> getAllCategories(int from, int size);

    CategoryDto getById(long catId, HttpServletRequest httpServletRequest);
}
