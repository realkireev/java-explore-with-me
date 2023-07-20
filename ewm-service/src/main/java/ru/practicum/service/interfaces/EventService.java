package ru.practicum.service.interfaces;

import ru.practicum.dto.EventStatisticsDto;
import ru.practicum.dto.EventRequestDto;
import ru.practicum.dto.EventResponseDto;
import ru.practicum.dto.EventUpdateRequestDto;
import ru.practicum.model.Event;
import ru.practicum.model.EventState;
import ru.practicum.model.SortType;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    List<EventResponseDto> getEvents(List<Long> userIds, List<EventState> states, List<Long> catIds,
                                            LocalDateTime start, LocalDateTime end, int from, int size);

    List<EventResponseDto> getPublishedEvents(String text, List<Long> catIds, Boolean paid,
                                                     LocalDateTime start, LocalDateTime end, Boolean onlyAvailable,
                                                     SortType sortType, int from, int size);

    EventResponseDto getPublishedEventById(Long eventId);

    List<EventResponseDto> getEventsByUserId(Long userId, int from, int size);

    EventResponseDto getEventByIdAndUserId(Long userId, Long eventId);

    EventResponseDto createEvent(Long userId, EventRequestDto eventRequestDto);

    EventResponseDto updateEvent(Long userId, Long eventId, EventUpdateRequestDto eventUpdateRequestDto);

    EventResponseDto handleEventPublication(Long eventId, EventUpdateRequestDto eventUpdateRequestDto);

    Event findEventById(Long eventId);

    List<EventStatisticsDto> getEventCountByState();
}
