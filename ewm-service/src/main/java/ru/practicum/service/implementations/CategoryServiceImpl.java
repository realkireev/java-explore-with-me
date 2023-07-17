package ru.practicum.service.implementations;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.common.CustomPageRequest;
import ru.practicum.dto.CategoryRequestDto;
import ru.practicum.dto.CategoryResponseDto;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.model.Category;
import ru.practicum.repo.CategoryRepository;
import ru.practicum.service.interfaces.CategoryService;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.common.Variables.CATEGORY_WAS_NOT_FOUND_MESSAGE;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryResponseDto> getCategories(int from, int size) {
        return categoryRepository.findAll(CustomPageRequest.of(from, size)).stream()
                .map(categoryMapper::toCategoryResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryResponseDto getCategoryById(Long categoryId) {
        return categoryMapper.toCategoryResponseDto(findCategoryById(categoryId));
    }

    @Override
    public CategoryResponseDto saveCategory(CategoryRequestDto categoryRequestDto) {
        return categoryMapper.toCategoryResponseDto(categoryRepository.saveAndFlush(categoryMapper
                .toCategory(categoryRequestDto)));
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        existsCategoryById(categoryId);
        categoryRepository.deleteById(categoryId);
    }

    @Override
    @Transactional
    public CategoryResponseDto updateCategory(Long categoryId, CategoryRequestDto categoryRequestDto) {
        Category category = findCategoryById(categoryId);
        category.setName(categoryRequestDto.getName());

        categoryRepository.saveAndFlush(category);
        return categoryMapper.toCategoryResponseDto(category);
    }

    private Category findCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ObjectNotFoundException(CATEGORY_WAS_NOT_FOUND_MESSAGE, categoryId));
    }

    private void existsCategoryById(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throwObjectNotFoundException(categoryId);
        }
    }

    private void throwObjectNotFoundException(Long categoryId) {
        throw new ObjectNotFoundException(CATEGORY_WAS_NOT_FOUND_MESSAGE, categoryId);
    }
}
