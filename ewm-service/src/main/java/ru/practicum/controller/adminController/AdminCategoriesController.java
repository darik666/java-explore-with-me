package ru.practicum.controller.adminController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.service.categories.CategoriesService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

/**
 * Контроллер категорий администратора
 */
@Valid
@RestController
@RequestMapping(path = "/admin/categories")
public class AdminCategoriesController {
    private final CategoriesService service;

    @Autowired
    public AdminCategoriesController(CategoriesService service) {
        this.service = service;
    }

    /**
     * Создание категории
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@RequestBody @Valid NewCategoryDto dto) {
        return service.create(dto);
    }

    /**
     * Удаление категории
     */
    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable @Positive Long catId) {
        service.delete(catId);
    }

    /**
     * Обновление категории
     */
    @PatchMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto updateCategory(@PathVariable @Positive Long catId,
                                      @RequestBody @Valid CategoryDto dto) {
        return service.update(catId, dto);
    }
}