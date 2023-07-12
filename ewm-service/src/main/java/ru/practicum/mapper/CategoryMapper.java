package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.dto.CategoryRequestDto;
import ru.practicum.dto.CategoryResponseDto;
import ru.practicum.model.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryResponseDto toCategoryResponseDto(Category category);

    Category toCategory(CategoryRequestDto categoryRequestDto);
}
