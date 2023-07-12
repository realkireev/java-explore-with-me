package ru.practicum.controller.publicapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.CategoryResponseDto;
import ru.practicum.service.CategoryService;
import ru.practicum.service.StatisticsService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Min;
import java.util.List;

import static ru.practicum.common.Variables.FROM_BELOW_ZERO_MESSAGE;
import static ru.practicum.common.Variables.FROM_DEFAULT;
import static ru.practicum.common.Variables.SIZE_BELOW_ZERO_MESSAGE;
import static ru.practicum.common.Variables.SIZE_DEFAULT;

@RestController
@RequestMapping(path = "/categories")
@Validated
@RequiredArgsConstructor
public class PublicCategoryController {
    private final CategoryService categoryService;
    private final StatisticsService statisticsService;

    @GetMapping
    public List<CategoryResponseDto> getAllCategories(
            @RequestParam(defaultValue = FROM_DEFAULT) @Min(value = 0, message = FROM_BELOW_ZERO_MESSAGE) int from,
            @RequestParam(defaultValue = SIZE_DEFAULT) @Min(value = 0, message = SIZE_BELOW_ZERO_MESSAGE) int size,
            HttpServletRequest request) throws JsonProcessingException {

        List<CategoryResponseDto> result = categoryService.getCategories(from, size);
        statisticsService.saveStatistics(request);
        return result;
    }

    @GetMapping(path = "/{categoryId}")
    public CategoryResponseDto getCategoryById(@PathVariable Long categoryId, HttpServletRequest request)
            throws JsonProcessingException {

        CategoryResponseDto result = categoryService.getCategoryById(categoryId);
        statisticsService.saveStatistics(request);
        return result;
    }
}
