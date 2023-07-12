package ru.practicum.controller.publicapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.EventResponseDto;
import ru.practicum.model.SortType;
import ru.practicum.service.EventService;
import ru.practicum.service.StatisticsService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.common.Variables.FROM_DEFAULT;
import static ru.practicum.common.Variables.SIZE_DEFAULT;

@RestController
@RequestMapping(path = "/events")
@Validated
@RequiredArgsConstructor
public class PublicEventsController {
    private final EventService eventService;
    private final StatisticsService statisticsService;

    @GetMapping
    public List<EventResponseDto> getPublishedEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) LocalDateTime rangeStart,
            @RequestParam(required = false) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false) SortType sort,
            @RequestParam(defaultValue = FROM_DEFAULT) int from,
            @RequestParam(defaultValue = SIZE_DEFAULT) int size,
            HttpServletRequest request) throws JsonProcessingException {

        List<EventResponseDto> result = eventService.getPublishedEvents(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, from, size);
        statisticsService.saveStatistics(request);

        return result;
    }

    @GetMapping(path = "/{eventId}")
    public EventResponseDto getPublishedEventById(@PathVariable Long eventId, HttpServletRequest request)
            throws JsonProcessingException {

        EventResponseDto result = eventService.getPublishedEventById(eventId);
        statisticsService.saveStatistics(request);

        return result;
    }
}
