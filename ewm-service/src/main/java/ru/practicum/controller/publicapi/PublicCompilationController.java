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
import ru.practicum.dto.CompilationResponseDto;
import ru.practicum.service.interfaces.CompilationService;
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
@RequestMapping(path = "/compilations")
@Validated
@RequiredArgsConstructor
@Slf4j
public class PublicCompilationController {
    private final CompilationService compilationService;
    private final StatisticsService statisticsService;

    @GetMapping
    public List<CompilationResponseDto> getAllCompilations(
            @RequestParam(required = false) Boolean pinned,
            @RequestParam(defaultValue = FROM_DEFAULT) @PositiveOrZero(message = FROM_BELOW_ZERO_MESSAGE) int from,
            @RequestParam(defaultValue = SIZE_DEFAULT) @Positive(message = SIZE_NOT_POSITIVE_MESSAGE) int size,
            HttpServletRequest request) throws JsonProcessingException {
        log.debug("GET /compilations - Getting all compilations with params: pinned={}, from={}, size={}", pinned,
                from, size);

        List<CompilationResponseDto> result = compilationService.getCompilations(pinned, from, size);
        statisticsService.saveStatistics(request);

        return result;
    }

    @GetMapping(path = "/{compilationId}")
    public CompilationResponseDto getCompilationById(@PathVariable Long compilationId, HttpServletRequest request)
            throws JsonProcessingException {

        log.debug("GET /compilations/{} - Getting compilation by id", compilationId);
        CompilationResponseDto result = compilationService.getCompilationById(compilationId);
        statisticsService.saveStatistics(request);
        return result;
    }
}
