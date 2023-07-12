package ru.practicum.controller.adminapi;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.EventResponseDto;
import ru.practicum.dto.EventUpdateRequestDto;
import ru.practicum.model.EventState;
import ru.practicum.service.EventService;

import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.common.Variables.FROM_BELOW_ZERO_MESSAGE;
import static ru.practicum.common.Variables.FROM_DEFAULT;
import static ru.practicum.common.Variables.SIZE_BELOW_ZERO_MESSAGE;
import static ru.practicum.common.Variables.SIZE_DEFAULT;

@RestController
@RequestMapping(path = "/admin/events")
@Validated
@RequiredArgsConstructor
public class AdminEventController {
    private final EventService eventService;

    @GetMapping
    public List<EventResponseDto> getAllEvents(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<EventState> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) LocalDateTime rangeStart,
            @RequestParam(required = false) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = FROM_DEFAULT) @Min(value = 0, message = FROM_BELOW_ZERO_MESSAGE) int from,
            @RequestParam(defaultValue = SIZE_DEFAULT) @Min(value = 0, message = SIZE_BELOW_ZERO_MESSAGE) int size) {

        return eventService.getEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping(path = "/{eventId}")
    public EventResponseDto publishOrCancelEvent(@PathVariable Long eventId,
                                                 @RequestBody EventUpdateRequestDto eventUpdateRequestDto) {
        return eventService.publishOrCancelEvent(eventId, eventUpdateRequestDto);
    }
}
