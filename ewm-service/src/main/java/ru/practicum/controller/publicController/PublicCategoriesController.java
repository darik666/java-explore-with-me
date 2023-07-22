package ru.practicum.controller.publicController;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CategoryDto;
import ru.practicum.service.categories.CategoriesService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/categories")
public class PublicCategoriesController {
    private final CategoriesService categoriesService;

    @GetMapping
    public Collection<CategoryDto> getAllCategories(@RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                          @RequestParam(defaultValue = "10") @Positive int size) {

        return categoriesService.getAllCategories(from, size);
    }

    @GetMapping("/{catId}")
    public CategoryDto getById(@PathVariable @Positive long catId) {
        return categoriesService.getById(catId);
    }
}
