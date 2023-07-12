package ru.practicum.controller.publicapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.CompilationResponseDto;
import ru.practicum.service.CompilationService;
import ru.practicum.service.StatisticsService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Min;
import java.util.List;

import static ru.practicum.common.Variables.FROM_BELOW_ZERO_MESSAGE;
import static ru.practicum.common.Variables.FROM_DEFAULT;
import static ru.practicum.common.Variables.SIZE_BELOW_ZERO_MESSAGE;
import static ru.practicum.common.Variables.SIZE_DEFAULT;

@RestController
@RequestMapping(path = "/compilations")
@Validated
@RequiredArgsConstructor
public class PublicCompilationController {
    private final CompilationService compilationService;
    private final StatisticsService statisticsService;

    @GetMapping
    public List<CompilationResponseDto> getAllCategories(
            @RequestParam(required = false) Boolean pinned,
            @RequestParam(defaultValue = FROM_DEFAULT) @Min(value = 0, message = FROM_BELOW_ZERO_MESSAGE) int from,
            @RequestParam(defaultValue = SIZE_DEFAULT) @Min(value = 0, message = SIZE_BELOW_ZERO_MESSAGE) int size,
            HttpServletRequest request) throws JsonProcessingException {

        List<CompilationResponseDto> result = compilationService.getCompilations(pinned, from, size);
        statisticsService.saveStatistics(request);

        return result;
    }

    @GetMapping(path = "/{compilationId}")
    public CompilationResponseDto getCompilationById(@PathVariable Long compilationId, HttpServletRequest request)
            throws JsonProcessingException {

        CompilationResponseDto result = compilationService.getCompilationById(compilationId);
        statisticsService.saveStatistics(request);
        return result;
    }
}
