package ru.practicum.controller.adminapi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.EventCountByStateDto;
import ru.practicum.dto.EventResponseDto;
import ru.practicum.dto.EventUpdateRequestDto;
import ru.practicum.model.EventState;
import ru.practicum.service.interfaces.EventService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.common.Variables.FROM_BELOW_ZERO_MESSAGE;
import static ru.practicum.common.Variables.FROM_DEFAULT;
import static ru.practicum.common.Variables.SIZE_DEFAULT;
import static ru.practicum.common.Variables.SIZE_NOT_POSITIVE_MESSAGE;

@RestController
@RequestMapping(path = "/admin/events")
@Validated
@RequiredArgsConstructor
@Slf4j
public class AdminEventController {
    private final EventService eventService;

    @GetMapping
    public List<EventResponseDto> getAllEvents(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<EventState> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) LocalDateTime rangeStart,
            @RequestParam(required = false) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = FROM_DEFAULT) @PositiveOrZero(message = FROM_BELOW_ZERO_MESSAGE) int from,
            @RequestParam(defaultValue = SIZE_DEFAULT) @Positive(message = SIZE_NOT_POSITIVE_MESSAGE) int size) {

        log.debug("GET /admin/events - Getting events with params: users={}, states={}, categories={}, rangeStart={}, " +
                "rangeEnd={}, from={}, size={}", users, states, categories, rangeStart, rangeEnd, from, size);

        return eventService.getEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @GetMapping(path = "/pending")
    public List<EventResponseDto> getPendingEvents(
            @RequestParam(defaultValue = FROM_DEFAULT) @PositiveOrZero(message = FROM_BELOW_ZERO_MESSAGE) int from,
            @RequestParam(defaultValue = SIZE_DEFAULT) @Positive(message = SIZE_NOT_POSITIVE_MESSAGE) int size) {
        log.debug("GET /admin/events/pending - Getting pending events with params: from={}, size={}", from, size);

        return eventService.getEvents(null, List.of(EventState.PENDING), null, null, null, from,
                size);
    }

    @GetMapping(path = "/under-revision")
    public List<EventResponseDto> getEventsUnderRevision(
            @RequestParam(defaultValue = FROM_DEFAULT) @PositiveOrZero(message = FROM_BELOW_ZERO_MESSAGE) int from,
            @RequestParam(defaultValue = SIZE_DEFAULT) @Positive(message = SIZE_NOT_POSITIVE_MESSAGE) int size) {
        log.debug("GET /admin/events/under-revision - Getting events under revision with params: from={}, size={}",
                from, size);

        return eventService.getEvents(null, List.of(EventState.UNDER_REVISION), null, null, null,
                from, size);
    }

    @GetMapping(path = "/statistics")
    public List<EventCountByStateDto> getEventCountByState() {
        log.debug("GET /admin/events/statistics - Getting statistics of events by state");

        return eventService.getEventCountByState();
    }

    @PatchMapping(path = "/{eventId}")
    public EventResponseDto handleEventPublication(@PathVariable Long eventId,
                                                   @RequestBody EventUpdateRequestDto eventUpdateRequestDto) {
        log.debug("PATCH /admin/events/{} - Handling event publication: {}", eventId, eventUpdateRequestDto);

        return eventService.handleEventPublication(eventId, eventUpdateRequestDto);
    }
}
