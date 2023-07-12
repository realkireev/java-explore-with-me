package ru.practicum.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.common.Validator;
import ru.practicum.dto.EventRequestDto;
import ru.practicum.dto.EventResponseDto;
import ru.practicum.dto.EventUpdateRequestDto;
import ru.practicum.dto.HitResponseDto;
import ru.practicum.exception.IllegalActionException;
import ru.practicum.exception.IllegalParametersException;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Action;
import ru.practicum.model.Event;
import ru.practicum.model.EventState;
import ru.practicum.model.QEvent;
import ru.practicum.model.RequestStatus;
import ru.practicum.model.SortType;
import ru.practicum.model.User;
import ru.practicum.repo.EventRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final UserService userService;
    private final RequestService requestService;
    private final StatisticsService statisticsService;
    private final Validator validator;

    @Transactional(readOnly = true)
    public List<EventResponseDto> getEvents(List<Long> userIds, List<EventState> states, List<Long> catIds,
                                            LocalDateTime start, LocalDateTime end, int from, int size) {

        QEvent event = QEvent.event;
        BooleanExpression bySearchCriteria = event.isNotNull();

        if (userIds != null) {
            bySearchCriteria = bySearchCriteria.and(event.initiator.id.in(userIds));
        }
        if (states != null) {
            bySearchCriteria = bySearchCriteria.and(event.state.in(states));
        }
        if (catIds != null) {
            bySearchCriteria = bySearchCriteria.and(event.category.id.in(catIds));
        }
        if (start != null) {
            bySearchCriteria = bySearchCriteria.and(event.eventDate.after(start));
        }
        if (end != null) {
            bySearchCriteria = bySearchCriteria.and(event.eventDate.before(end));
        }

        List<Event> foundItems = eventRepository.findAll(bySearchCriteria, PageRequest.of(from / size, size))
                .getContent();

        return foundItems.stream()
                .map(eventMapper::toEventResponseDto)
                .map(x -> addStatistics(x, start, end))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EventResponseDto> getPublishedEvents(String text, List<Long> catIds, Boolean paid,
                                                     LocalDateTime start, LocalDateTime end, Boolean onlyAvailable,
                                                     SortType sortType, int from, int size) {

        validator.validateConsequentDates(start, end);

        QEvent event = QEvent.event;
        BooleanExpression bySearchCriteria = event.state.eq(EventState.PUBLISHED);
        if (text != null) {
            bySearchCriteria = bySearchCriteria.and(event.annotation.contains(text)
                    .or(event.description.contains(text)));
        }
        if (catIds != null) {
            bySearchCriteria = bySearchCriteria.and(event.category.id.in(catIds));
        }
        if (paid != null) {
            bySearchCriteria = bySearchCriteria.and(event.paid.eq(paid));
        }
        if (onlyAvailable != null && onlyAvailable) {
            List<Long> availableEventIds = eventRepository.findAvailableEventIds();
            bySearchCriteria = bySearchCriteria.and(event.id.in(availableEventIds));
        }
        if (start != null) {
            bySearchCriteria = bySearchCriteria.and(QEvent.event.eventDate.after(start));
        }
        if (end != null) {
            bySearchCriteria = bySearchCriteria.and(QEvent.event.eventDate.before(end));
        }

        Sort sort = Sort.unsorted();
        if (sortType != null && sortType.equals(SortType.EVENT_DATE)) {
            sort = Sort.by(Sort.Direction.ASC, "eventDate");
        }

        List<Event> result = eventRepository.findAll(bySearchCriteria, PageRequest.of(from / size, size, sort))
                .getContent();

        if (sortType != null && sortType.equals(SortType.VIEWS)) {
            return sortByViews(result, start, end);
        }

        return result.stream()
                .map(eventMapper::toEventResponseDto)
                .collect(Collectors.toList());
    }

    public EventResponseDto getPublishedEventById(Long eventId) {
        Event result = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED);
        if (result == null) {
            throw new ObjectNotFoundException("Event with id=%d was not found.", eventId);
        }

        return addStatistics(eventMapper.toEventResponseDto(result), null, null);
    }

    public List<EventResponseDto> getEventsByUserId(Long userId, int from, int size) {
        return eventRepository.findAllByInitiatorId(userId, PageRequest.of(from / size, size))
                .stream()
                .map(eventMapper::toEventResponseDto)
                .collect(Collectors.toList());
    }

    public EventResponseDto getEventByIdAndUserId(Long userId, Long eventId) {
        userService.existUserById(userId);
        Optional<Event> event = eventRepository.findByIdAndInitiatorId(eventId, userId);
        if (event.isEmpty()) {
            throwObjectNotFoundException(eventId);
        }

        return eventMapper.toEventResponseDto(event.get());
    }

    public EventResponseDto createEvent(Long userId, EventRequestDto eventRequestDto) {
        User user = userService.findUserById(userId);

        Event event = eventMapper.toEvent(eventRequestDto);
        event.setInitiator(user);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventState.PENDING);

        return eventMapper.toEventResponseDto(eventRepository.saveAndFlush(event));
    }

    public EventResponseDto updateEvent(Long userId, Long eventId, EventUpdateRequestDto eventUpdateRequestDto) {
        validator.validate(eventUpdateRequestDto);

        userService.findUserById(userId);
        Event storedEvent = findEventById(eventId);

        if (storedEvent.getState().equals(EventState.PUBLISHED)) {
            throw new IllegalActionException("Changing requests after publishing is not allowed.");
        }

        Action stateAction = eventUpdateRequestDto.getStateAction();
        if (stateAction != null) {
            switch (getAction(eventUpdateRequestDto)) {
                case SEND_TO_REVIEW:
                    storedEvent.setState(EventState.PENDING);
                    break;

                case REJECT_EVENT:
                case CANCEL_REVIEW:
                    storedEvent.setState(EventState.CANCELED);
                    break;
            }
        }

        return eventMapper.toEventResponseDto(storedEvent);
    }

    public EventResponseDto publishOrCancelEvent(Long eventId, EventUpdateRequestDto eventUpdateRequestDto) {
        validator.validate(eventUpdateRequestDto);

        Event storedEvent = findEventById(eventId);
        eventMapper.toEvent(eventUpdateRequestDto, storedEvent);

        Action stateAction = eventUpdateRequestDto.getStateAction();
        if (stateAction != null) {
            switch (stateAction) {
                case PUBLISH_EVENT:
                    if (storedEvent.getState().equals(EventState.PUBLISHED)) {
                        throw new IllegalActionException("Event is already published.");
                    }

                    if (storedEvent.getState().equals(EventState.CANCELED)) {
                        throw new IllegalActionException("Impossible to publish a cancelled event.");
                    }

                    storedEvent.setState(EventState.PUBLISHED);
                    storedEvent.setPublishedOn(LocalDateTime.now());
                    break;

                case REJECT_EVENT:
                    if (storedEvent.getState().equals(EventState.PUBLISHED)) {
                        throw new IllegalActionException("Impossible to cancel a published event.");
                    }

                    storedEvent.setState(EventState.CANCELED);
                    break;
            }
        }

        return eventMapper.toEventResponseDto(eventRepository.saveAndFlush(storedEvent));
    }

    public Event findEventById(Long eventId) {
        Optional<Event> result = eventRepository.findById(eventId);

        if (result.isEmpty()) {
            throwObjectNotFoundException(eventId);
        }
        return result.get();
    }

    public void existsEventById(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throwObjectNotFoundException(eventId);
        }
    }

    private void throwObjectNotFoundException(Long eventId) {
        throw new ObjectNotFoundException("Event with id=%d was not found", eventId);
    }

    private EventResponseDto addStatistics(EventResponseDto erd, LocalDateTime start, LocalDateTime end) {
        long confirmedRequests = requestService.countRequestsByEventIdAndStatus(erd.getId(), RequestStatus.CONFIRMED);
        erd.setConfirmedRequests(confirmedRequests);

        List<HitResponseDto> stat = statisticsService.getStatistics(start, end, List.of("/events/" + erd.getId()),
                true);

        if (stat != null) {
            erd.setViews(stat.get(0).getHits());
        }

        return erd;
    }

    private Action getAction(EventUpdateRequestDto eventUpdateRequestDto) {
        Action stateAction = eventUpdateRequestDto.getStateAction();

        if (stateAction == null) {
            throw new IllegalParametersException("StateAction argument is missing.");
        }
        return stateAction;
    }

    private List<EventResponseDto> sortByViews(List<Event> events, LocalDateTime start, LocalDateTime end) {
        List<String> uris = events.stream()
                .map(Event::getId)
                .map(String::valueOf)
                .map(x -> "/events/" + x)
                .collect(Collectors.toList());

        List<HitResponseDto> stat = statisticsService.getStatistics(start, end, uris, true);

        Map<Long, Long> views = new HashMap<>();

        List<EventResponseDto> eventDtoList = events.stream()
                .map(eventMapper::toEventResponseDto).collect(Collectors.toList());

        if (stat != null) {
            stat.stream()
                    .filter(x -> x.getUri().contains("events"))
                    .filter(x -> x.getUri().split("/").length == 3)
                    .forEach(x -> {
                        Long id = Long.parseLong(x.getUri().split("/")[2]);
                        Long hits = x.getHits();
                        views.put(id, hits);
                    });

            for (EventResponseDto event : eventDtoList) {
                event.setViews(views.getOrDefault(event.getId(), 0L));
            }

            eventDtoList.sort((x, y) -> y.getViews().intValue() - x.getViews().intValue());
        }

        return eventDtoList;
    }
}
