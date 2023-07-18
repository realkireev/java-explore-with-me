package ru.practicum.service.interfaces;

import ru.practicum.dto.CategoryRequestDto;
import ru.practicum.dto.CategoryResponseDto;

import java.util.List;

public interface CategoryService {
    List<CategoryResponseDto> getCategories(int from, int size);

    CategoryResponseDto getCategoryById(Long categoryId);

    CategoryResponseDto saveCategory(CategoryRequestDto categoryRequestDto);

    void deleteCategory(Long categoryId);

    CategoryResponseDto updateCategory(Long categoryId, CategoryRequestDto categoryRequestDto);
}
