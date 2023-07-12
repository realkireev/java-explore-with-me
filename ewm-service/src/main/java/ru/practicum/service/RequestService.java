package ru.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.practicum.dto.ConfirmRequestDto;
import ru.practicum.dto.ConfirmResponseDto;
import ru.practicum.dto.RequestResponseDto;
import ru.practicum.exception.IllegalActionException;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.Event;
import ru.practicum.model.EventState;
import ru.practicum.model.Request;
import ru.practicum.model.RequestStatus;
import ru.practicum.repo.RequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RequestService {
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;
    private final UserService userService;
    private final EventService eventService;

    public RequestService(RequestRepository requestRepository, RequestMapper requestMapper, UserService userService,
                          @Lazy EventService eventService) {
        this.requestRepository = requestRepository;
        this.requestMapper = requestMapper;
        this.userService = userService;
        this.eventService = eventService;
    }

    public List<RequestResponseDto> getRequestsByUserId(Long userId) {
        userService.existUserById(userId);

        List<Request> result = requestRepository.findAllByRequesterId(userId);

        return result.stream()
                .map(requestMapper::toRequestResponseDto)
                .collect(Collectors.toList());
    }

    public List<RequestResponseDto> getRequestsByUserIdAndEventId(Long userId, Long eventId) {
        userService.existUserById(userId);
        eventService.existsEventById(eventId);

        List<Request> requests = requestRepository.findAllByEventId(eventId);

        return requests.stream()
                .map(requestMapper::toRequestResponseDto)
                .collect(Collectors.toList());
    }

    public RequestResponseDto createRequest(Long userId, Long eventId) {
        userService.existUserById(userId);
        Event event = eventService.findEventById(eventId);

        if (event.getInitiator().getId().equals(userId)) {
            throw new IllegalActionException("Requests from the initiator are not allowed.");
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new IllegalActionException("Requests for unpublished events are not allowed.");
        }

        RequestStatus status;
        if (event.getParticipantLimit() == 0) {
            status = RequestStatus.CONFIRMED;
        } else {
            int countRequests = requestRepository.countAllByEventId(eventId);

            if (countRequests == event.getParticipantLimit()) {
                throw new IllegalActionException("The event capacity has reached its limit.");
            }

            status = RequestStatus.PENDING;
        }

        Request request = Request.builder()
                .requesterId(userId)
                .eventId(eventId)
                .created(LocalDateTime.now())
                .status(status)
                .build();

        return requestMapper.toRequestResponseDto(requestRepository.saveAndFlush(request));
    }

    public ConfirmResponseDto confirmOrCancelRequest(Long userId, Long eventId, ConfirmRequestDto confirmRequestDto) {
        if (confirmRequestDto == null) {
            throw new IllegalActionException("Empty payload is not allowed.");
        }

        userService.existUserById(userId);
        eventService.existsEventById(eventId);

        if (confirmRequestDto.getRequestIds() != null) {
            if (requestRepository.countAllByStatusAndIdIn(RequestStatus.CONFIRMED, confirmRequestDto.getRequestIds()) > 0) {
                throw new IllegalActionException("Cancelling confirmed requests is not allowed.");
            }
        }

        requestRepository.updateRequestStatus(confirmRequestDto.getStatus(), confirmRequestDto.getRequestIds());

        List<RequestResponseDto> confirmedRequests = requestRepository.findAllByEventIdAndStatus(eventId, RequestStatus.CONFIRMED).stream()
                .map(requestMapper::toRequestResponseDto)
                .collect(Collectors.toList());

        List<RequestResponseDto> rejectedRequests = requestRepository.findAllByEventIdAndStatus(eventId, RequestStatus.REJECTED).stream()
                .map(requestMapper::toRequestResponseDto)
                .collect(Collectors.toList());

        return ConfirmResponseDto.builder()
                .confirmedRequests(confirmedRequests)
                .rejectedRequests(rejectedRequests)
                .build();
    }

    public RequestResponseDto cancelRequest(Long userId, Long requestId) {
        userService.existUserById(userId);
        Request request = findRequestById(requestId);
        request.setStatus(RequestStatus.CANCELED);

        return requestMapper.toRequestResponseDto(requestRepository.saveAndFlush(request));
    }

    public int countRequestsByEventIdAndStatus(Long eventId, RequestStatus status) {
        return requestRepository.countAllByEventIdAndStatus(eventId, status);
    }

    private Request findRequestById(Long requestId) {
        Optional<Request> request = requestRepository.findById(requestId);
        if (request.isEmpty()) {
            throw new ObjectNotFoundException("Request with id=%s was not found.", requestId);
        }

        return request.get();
    }
}
