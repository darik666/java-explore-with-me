package ru.practicum.controller.adminController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.NewCategoryDto;
import ru.practicum.service.categories.CategoriesService;

import javax.validation.constraints.Positive;

@RestController
@RequestMapping(path = "/admin/categories")
public class AdminCategoriesController {
    private final CategoriesService service;

    @Autowired
    public AdminCategoriesController(CategoriesService service) {
        this.service = service;
    }

    @PostMapping
    public CategoryDto createCategory(@RequestBody NewCategoryDto dto) {
        return service.create(dto);
    }

    @DeleteMapping("/{catId}")
    public void deleteCategory(@PathVariable @Positive Long catId) {
        service.delete(catId);
    }

    @PatchMapping
    public CategoryDto updateCategory(@RequestBody CategoryDto dto) {
        return service.update(dto);
    }

}
