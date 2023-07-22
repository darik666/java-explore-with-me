package ru.practicum.service.categories;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.NewCategoryDto;
import ru.practicum.exception.AlreadyExistsException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Category;
import ru.practicum.repository.CategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CategoriesServiceImpl implements CategoriesService {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryDto create(NewCategoryDto dto) {
        return CategoryMapper.toCategoryDto(categoryRepository.save(CategoryMapper.toCategory(dto)));
    }

    @Override
    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public CategoryDto update(CategoryDto dto) {
        Category cat = categoryRepository
                .findById(dto.getId())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Категория с id=%d не найдена", dto.getId())));
        if (categoryRepository.findByName(dto.getName()).isPresent()) {
            throw new AlreadyExistsException(
                    String.format("Категория - %s уже присутствует", dto.getName()));
        }
        cat.setName(dto.getName());
        return CategoryMapper.toCategoryDto(categoryRepository.save(cat));
    }

    @Override
    public List<CategoryDto> getAllCategories(int from, int size) {
        Pageable pageable = PageRequest.of(from, size);
        List<Category> categories = categoryRepository.getAllCategoriesWithPagination(pageable);
        return categories.stream().map(CategoryMapper::toCategoryDto).collect(Collectors.toList());
    }

    @Override
    public CategoryDto getById(long catId) {
        Category cat = categoryRepository
                .findById(catId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Категория с id=%d не найдена", catId)));
        return CategoryMapper.toCategoryDto(cat);
    }
}
