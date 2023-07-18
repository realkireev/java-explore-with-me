package ru.practicum.controller.publicapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.CategoryResponseDto;
import ru.practicum.service.interfaces.CategoryService;
import ru.practicum.service.interfaces.StatisticsService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.common.Variables.FROM_BELOW_ZERO_MESSAGE;
import static ru.practicum.common.Variables.FROM_DEFAULT;
import static ru.practicum.common.Variables.SIZE_DEFAULT;
import static ru.practicum.common.Variables.SIZE_NOT_POSITIVE_MESSAGE;

@RestController
@RequestMapping(path = "/categories")
@Validated
@RequiredArgsConstructor
@Slf4j
public class PublicCategoryController {
    private final CategoryService categoryService;
    private final StatisticsService statisticsService;

    @GetMapping
    public List<CategoryResponseDto> getAllCategories(
            @RequestParam(defaultValue = FROM_DEFAULT) @PositiveOrZero(message = FROM_BELOW_ZERO_MESSAGE) int from,
            @RequestParam(defaultValue = SIZE_DEFAULT) @Positive(message = SIZE_NOT_POSITIVE_MESSAGE) int size,
            HttpServletRequest request) throws JsonProcessingException {
        log.debug("GET /categories - Getting categories with params: from={}, size={}", from, size);

        List<CategoryResponseDto> result = categoryService.getCategories(from, size);
        statisticsService.saveStatistics(request);
        return result;
    }

    @GetMapping(path = "/{categoryId}")
    public CategoryResponseDto getCategoryById(@PathVariable Long categoryId, HttpServletRequest request)
            throws JsonProcessingException {
        log.debug("GET /categories/{} - Getting category", categoryId);

        CategoryResponseDto result = categoryService.getCategoryById(categoryId);
        statisticsService.saveStatistics(request);
        return result;
    }
}
