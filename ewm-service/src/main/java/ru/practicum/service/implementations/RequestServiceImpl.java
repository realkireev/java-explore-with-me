package ru.practicum.service.implementations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import ru.practicum.repo.EventRepository;
import ru.practicum.repo.RequestRepository;
import ru.practicum.repo.UserRepository;
import ru.practicum.service.interfaces.RequestService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.common.Variables.EVENT_WAS_NOT_FOUND_MESSAGE;
import static ru.practicum.common.Variables.REQUEST_WAS_NOT_FOUND_MESSAGE;
import static ru.practicum.common.Variables.USER_WAS_NOT_FOUND_MESSAGE;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public List<RequestResponseDto> getRequestsByUserId(Long userId) {
        existUserById(userId);

        List<Request> result = requestRepository.findAllByRequesterId(userId);

        return result.stream()
                .map(requestMapper::toRequestResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RequestResponseDto> getRequestsByUserIdAndEventId(Long userId, Long eventId) {
        existUserById(userId);
        existsEventById(eventId);

        List<Request> requests = requestRepository.findAllByEventId(eventId);

        return requests.stream()
                .map(requestMapper::toRequestResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RequestResponseDto createRequest(Long userId, Long eventId) {
        existUserById(userId);
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new ObjectNotFoundException(EVENT_WAS_NOT_FOUND_MESSAGE, eventId));

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

    @Override
    @Transactional
    public ConfirmResponseDto confirmOrCancelRequest(Long userId, Long eventId, ConfirmRequestDto confirmRequestDto) {
        if (confirmRequestDto == null) {
            throw new IllegalActionException("Empty payload is not allowed.");
        }

        existUserById(userId);
        existsEventById(eventId);

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

    @Override
    @Transactional
    public RequestResponseDto cancelRequest(Long userId, Long requestId) {
        existUserById(userId);
        Request request = findRequestById(requestId);
        request.setStatus(RequestStatus.CANCELED);

        return requestMapper.toRequestResponseDto(requestRepository.saveAndFlush(request));
    }

    private Request findRequestById(Long requestId) {
        return requestRepository.findById(requestId).orElseThrow(
                () -> new ObjectNotFoundException(REQUEST_WAS_NOT_FOUND_MESSAGE, requestId));
    }

    private void existUserById(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException(USER_WAS_NOT_FOUND_MESSAGE, userId);
        }
    }

    private void existsEventById(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new ObjectNotFoundException(EVENT_WAS_NOT_FOUND_MESSAGE, eventId);
        }
    }
}
