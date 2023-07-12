package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.dto.CategoryRequestDto;
import ru.practicum.dto.CategoryResponseDto;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.model.Category;
import ru.practicum.repo.CategoryRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryResponseDto> getCategories(int from, int size) {
        return categoryRepository.findAll(PageRequest.of(from / size, size)).stream()
                .map(categoryMapper::toCategoryResponseDto)
                .collect(Collectors.toList());
    }

    public List<Category> findAllCategoriesByIds(List<Long> ids) {
        return categoryRepository.findAllById(ids);
    }

    public CategoryResponseDto getCategoryById(Long categoryId) {
        return categoryMapper.toCategoryResponseDto(findCategoryById(categoryId));
    }

    public CategoryResponseDto saveCategory(CategoryRequestDto categoryRequestDto) {
        return categoryMapper.toCategoryResponseDto(categoryRepository.saveAndFlush(categoryMapper
                .toCategory(categoryRequestDto)));
    }

    public void deleteCategory(Long categoryId) {
        existsCategoryById(categoryId);
        categoryRepository.deleteById(categoryId);
    }

    public CategoryResponseDto updateCategory(Long categoryId, CategoryRequestDto categoryRequestDto) {
        Category category = findCategoryById(categoryId);
        category.setName(categoryRequestDto.getName());

        categoryRepository.saveAndFlush(category);
        return categoryMapper.toCategoryResponseDto(category);
    }

    private Category findCategoryById(Long categoryId) {
        Optional<Category> result = categoryRepository.findById(categoryId);

        if (result.isEmpty()) {
            throwObjectNotFoundException(categoryId);
        }

        return result.get();
    }

    private void existsCategoryById(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throwObjectNotFoundException(categoryId);
        }
    }

    private void throwObjectNotFoundException(Long categoryId) {
        throw new ObjectNotFoundException("Category with id=%d was not found", categoryId);
    }
}
