package ru.practicum.controller.publicController;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.service.categories.CategoriesService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

/**
 * Публичный контроллер категорий
 */
@RestController
@AllArgsConstructor
@RequestMapping(path = "/categories")
public class PublicCategoriesController {

    private final CategoriesService categoriesService;

    /**
     * Получение списка категорий
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<CategoryDto> getAllCategories(
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        return categoriesService.getAllCategories(from, size);
    }

    /**
     * Получение категорий по id
     */
    @GetMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto getById(@PathVariable @Positive Long catId,
                               HttpServletRequest httpServletRequest) {
        return categoriesService.getById(catId, httpServletRequest);
    }
}