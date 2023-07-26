package ru.practicum.service.categories;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.EwmClient;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.exception.AlreadyExistsException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервисный класс категорий
 */
@Service
@AllArgsConstructor
public class CategoriesServiceImpl implements CategoriesService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final EwmClient ewmClient;

    /**
     * Создание категории
     */
    @Transactional
    @Override
    public CategoryDto create(NewCategoryDto dto) {
        if (categoryRepository.findByName(dto.getName()).isPresent()) {
            throw new AlreadyExistsException("Категория с таким названием уже существует");
        }
        return CategoryMapper.toCategoryDto(categoryRepository.save(CategoryMapper.toCategoryfromNew(dto)));
    }

    /**
     * Удаление категории
     */
    @Transactional
    @Override
    public void delete(Long id) {
        List<Event> events = eventRepository.findByCategoryId(id);
        if (events.size() > 0) {
            throw new DataIntegrityViolationException("Категория связана с событиями");
        }
        categoryRepository.deleteById(id);
    }

    /**
     * Обновление категории
     */
    @Transactional
    @Override
    public CategoryDto update(Long catId, CategoryDto dto) {
        Category category = CategoryMapper.toCategoryUpdate(dto, catId);
        Category cat = categoryRepository
                .findById(catId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Категория с id=%d не найдена", dto.getId())));
        cat.setName(category.getName());
        return CategoryMapper.toCategoryDto(categoryRepository.save(cat));
    }

    /**
     * Получение списка категорий
     */
    @Override
    public List<CategoryDto> getAllCategories(int from, int size) {
        Pageable pageable = PageRequest.of(from, size);
        List<Category> categories = categoryRepository.getAllCategoriesWithPagination(pageable);
        return categories.stream().map(CategoryMapper::toCategoryDto).collect(Collectors.toList());
    }

    /**
     * Получение категории по id
     */
    @Override
    public CategoryDto getById(long catId, HttpServletRequest httpServletRequest) {
        Category cat = categoryRepository
                .findById(catId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Категория с id=%d не найдена", catId)));
        ewmClient.addHit(httpServletRequest);
        return CategoryMapper.toCategoryDto(cat);
    }
}