package ru.practicum.controller.adminapi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.CategoryRequestDto;
import ru.practicum.dto.CategoryResponseDto;
import ru.practicum.service.interfaces.CategoryService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping(path = "/admin/categories")
@Validated
@RequiredArgsConstructor
@Slf4j
public class AdminCategoryController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponseDto saveCategory(@Valid @NotNull @RequestBody CategoryRequestDto categoryRequestDto) {
        log.debug("POST /admin/categories - Saving category: {}", categoryRequestDto);

        return categoryService.saveCategory(categoryRequestDto);
    }

    @PatchMapping(path = "/{categoryId}")
    public CategoryResponseDto updateCategory(@PathVariable Long categoryId,
                                              @Valid @NotNull @RequestBody CategoryRequestDto categoryRequestDto) {
        log.debug("PATCH /admin/{} - Updating category: {}", categoryId, categoryRequestDto);

        return categoryService.updateCategory(categoryId, categoryRequestDto);
    }

    @DeleteMapping(path = "/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long categoryId) {
        log.debug("DELETE /admin/{} - Deleting category", categoryId);

        categoryService.deleteCategory(categoryId);
    }
}
