package ru.practicum.controller.privateapi;

import lombok.RequiredArgsConstructor;
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
import ru.practicum.service.EventService;
import ru.practicum.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

import static ru.practicum.common.Variables.FROM_BELOW_ZERO_MESSAGE;
import static ru.practicum.common.Variables.FROM_DEFAULT;
import static ru.practicum.common.Variables.SIZE_BELOW_ZERO_MESSAGE;
import static ru.practicum.common.Variables.SIZE_DEFAULT;

@RestController
@RequestMapping(path = "/users")
@Validated
@RequiredArgsConstructor
public class  PrivateUsersEventController {
    private final EventService eventService;
    private final RequestService requestService;

    @GetMapping(path = "/{userId}/events")
    public List<EventResponseDto> getEventsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = FROM_DEFAULT) @Min(value = 0, message = FROM_BELOW_ZERO_MESSAGE) int from,
            @RequestParam(defaultValue = SIZE_DEFAULT) @Min(value = 0, message = SIZE_BELOW_ZERO_MESSAGE) int size) {
        return eventService.getEventsByUserId(userId, from, size);
    }

    @GetMapping(path = "/{userId}/events/{eventId}")
    public EventResponseDto getEventByIdAndUserId(@PathVariable Long userId, @PathVariable Long eventId) {
        return eventService.getEventByIdAndUserId(userId, eventId);
    }

    @GetMapping(path = "/{userId}/events/{eventId}/requests")
    public List<RequestResponseDto> getRequests(@PathVariable Long userId, @PathVariable Long eventId) {
        return requestService.getRequestsByUserIdAndEventId(userId, eventId);
    }

    @GetMapping(path = "/{userId}/requests")
    public List<RequestResponseDto> getRequestsByUserId(@PathVariable Long userId) {
        return requestService.getRequestsByUserId(userId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "/{userId}/events")
    public EventResponseDto postEvent(@PathVariable Long userId, @Valid @RequestBody EventRequestDto eventRequestDto) {
        return eventService.createEvent(userId, eventRequestDto);
    }

    @PostMapping(path = "/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public RequestResponseDto postRequest(@PathVariable Long userId, @RequestParam Long eventId) {
        return requestService.createRequest(userId, eventId);
    }

    @PatchMapping(path = "/{userId}/events/{eventId}")
    public EventResponseDto updateEvent(@PathVariable Long userId, @PathVariable Long eventId,
                                        @RequestBody(required = false) EventUpdateRequestDto eventUpdateRequestDto) {
        return eventService.updateEvent(userId, eventId, eventUpdateRequestDto);
    }

    @PatchMapping(path = "/{userId}/events/{eventId}/requests")
    public ConfirmResponseDto confirmOrCancelRequest(@PathVariable Long userId,
                                                     @PathVariable Long eventId,
                                                     @RequestBody(required = false) ConfirmRequestDto crd) {
        return requestService.confirmOrCancelRequest(userId, eventId, crd);
    }

    @PatchMapping(path = "/{userId}/requests/{requestId}/cancel")
    public RequestResponseDto cancelRequest(@PathVariable Long userId, @PathVariable Long requestId) {
        return requestService.cancelRequest(userId, requestId);
    }
}
