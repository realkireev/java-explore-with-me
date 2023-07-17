package ru.practicum.service.interfaces;

import ru.practicum.dto.ConfirmRequestDto;
import ru.practicum.dto.ConfirmResponseDto;
import ru.practicum.dto.RequestResponseDto;

import java.util.List;

public interface RequestService {
    List<RequestResponseDto> getRequestsByUserId(Long userId);

    List<RequestResponseDto> getRequestsByUserIdAndEventId(Long userId, Long eventId);

    RequestResponseDto createRequest(Long userId, Long eventId);

    ConfirmResponseDto confirmOrCancelRequest(Long userId, Long eventId, ConfirmRequestDto confirmRequestDto);

    RequestResponseDto cancelRequest(Long userId, Long requestId);
}
