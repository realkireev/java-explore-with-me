package ru.practicum.controller.privateapi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.ConfirmRequestDto;
import ru.practicum.dto.ConfirmResponseDto;
import ru.practicum.dto.EventRequestDto;
import ru.practicum.dto.EventResponseDto;
import ru.practicum.dto.EventUpdateRequestDto;
import ru.practicum.dto.RequestResponseDto;
import ru.practicum.service.interfaces.EventService;
import ru.practicum.service.interfaces.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.common.Variables.FROM_BELOW_ZERO_MESSAGE;
import static ru.practicum.common.Variables.FROM_DEFAULT;
import static ru.practicum.common.Variables.SIZE_DEFAULT;
import static ru.practicum.common.Variables.SIZE_NOT_POSITIVE_MESSAGE;

@RestController
@RequestMapping(path = "/users/{userId}")
@Validated
@RequiredArgsConstructor
@Slf4j
public class  PrivateUsersEventController {
    private final EventService eventService;
    private final RequestService requestService;

    @GetMapping(path = "/events")
    public List<EventResponseDto> getEventsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = FROM_DEFAULT) @PositiveOrZero(message = FROM_BELOW_ZERO_MESSAGE) int from,
            @RequestParam(defaultValue = SIZE_DEFAULT) @Positive(message = SIZE_NOT_POSITIVE_MESSAGE) int size) {
        log.debug("GET /users/{}/events - Getting events by userId with params: from={}, size={}", userId, from, size);

        return eventService.getEventsByUserId(userId, from, size);
    }

    @GetMapping(path = "/events/{eventId}")
    public EventResponseDto getEventByIdAndUserId(@PathVariable Long userId, @PathVariable Long eventId) {
        log.debug("GET /users/{}/events/{} - Getting events by userId and eventId", userId, eventId);

        return eventService.getEventByIdAndUserId(userId, eventId);
    }

    @GetMapping(path = "/events/{eventId}/requests")
    public List<RequestResponseDto> getRequests(@PathVariable Long userId, @PathVariable Long eventId) {
        log.debug("GET /users/{}/events/{}/requests - Getting requests by userId and eventId", userId, eventId);

        return requestService.getRequestsByUserIdAndEventId(userId, eventId);
    }

    @GetMapping(path = "/requests")
    public List<RequestResponseDto> getRequestsByUserId(@PathVariable Long userId) {
        log.debug("GET /users/{}/requests - Getting requests by userId", userId);

        return requestService.getRequestsByUserId(userId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "/events")
    public EventResponseDto postEvent(@PathVariable Long userId, @Valid @RequestBody EventRequestDto eventRequestDto) {
        log.debug("POST /users/{}/events - Creating event: {}", userId, eventRequestDto);

        return eventService.createEvent(userId, eventRequestDto);
    }

    @PostMapping(path = "/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public RequestResponseDto postRequest(@PathVariable Long userId, @RequestParam Long eventId) {
        log.debug("POST /users/{}/requests - Creating request with param: eventId={}", userId, eventId);

        return requestService.createRequest(userId, eventId);
    }

    @PatchMapping(path = "/events/{eventId}")
    public EventResponseDto updateEvent(@PathVariable Long userId, @PathVariable Long eventId,
                                        @RequestBody(required = false) EventUpdateRequestDto eventUpdateRequestDto) {
        log.debug("PATCH /users/{}/events/{} - Updating event: {}", userId, eventId, eventUpdateRequestDto);

        return eventService.updateEvent(userId, eventId, eventUpdateRequestDto);
    }

    @PatchMapping(path = "/events/{eventId}/requests")
    public ConfirmResponseDto confirmOrCancelRequest(@PathVariable Long userId,
                                                     @PathVariable Long eventId,
                                                     @RequestBody(required = false) ConfirmRequestDto crd) {
        log.debug("PATCH /users/{}/events/{}/requests - Confirming or cancelling request: {}", userId, eventId, crd);

        return requestService.confirmOrCancelRequest(userId, eventId, crd);
    }

    @PatchMapping(path = "/requests/{requestId}/cancel")
    public RequestResponseDto cancelRequest(@PathVariable Long userId, @PathVariable Long requestId) {
        log.debug("PATCH /users/{}/requests/{}/cancel - Cancelling request", userId, requestId);

        return requestService.cancelRequest(userId, requestId);
    }
}
