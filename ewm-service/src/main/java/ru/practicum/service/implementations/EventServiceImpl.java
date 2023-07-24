package ru.practicum.service.implementations;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.common.CustomPageRequest;
import ru.practicum.common.CustomValidator;
import ru.practicum.dto.EventRequestDto;
import ru.practicum.dto.EventResponseDto;
import ru.practicum.dto.EventStatisticsDto;
import ru.practicum.dto.EventUpdateRequestDto;
import ru.practicum.dto.HitResponseDto;
import ru.practicum.exception.IllegalActionException;
import ru.practicum.exception.IllegalParametersException;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.EventStatisticsMapper;
import ru.practicum.model.Action;
import ru.practicum.model.Event;
import ru.practicum.model.EventState;
import ru.practicum.model.EventStatistics;
import ru.practicum.model.QEvent;
import ru.practicum.model.RequestStatus;
import ru.practicum.model.SortType;
import ru.practicum.model.User;
import ru.practicum.repo.EventRepository;
import ru.practicum.repo.EventStatisticsRepository;
import ru.practicum.repo.RequestRepository;
import ru.practicum.repo.UserRepository;
import ru.practicum.service.interfaces.EventService;
import ru.practicum.service.interfaces.StatisticsService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.common.Variables.EVENT_WAS_NOT_FOUND_MESSAGE;
import static ru.practicum.common.Variables.USER_WAS_NOT_FOUND_MESSAGE;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final EventStatisticsRepository eventStatisticsRepository;
    private final EventStatisticsMapper eventStatisticsMapper;
    private final StatisticsService statisticsService;

    @Override
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

        List<Event> foundItems = eventRepository.findAll(bySearchCriteria, CustomPageRequest.of(from, size))
                .getContent();

        return foundItems.stream()
                .map(eventMapper::toEventResponseDto)
                .map(x -> addStatistics(x, start, end))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponseDto> getPublishedEvents(String text, List<Long> catIds, Boolean paid,
                                                     LocalDateTime start, LocalDateTime end, Boolean onlyAvailable,
                                                     SortType sortType, int from, int size) {

        CustomValidator.validateConsequentDates(start, end);

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

        List<Event> result = eventRepository.findAll(bySearchCriteria, CustomPageRequest.of(from, size, sort))
                .getContent();

        if (sortType != null && sortType.equals(SortType.VIEWS)) {
            return sortByViews(result, start, end);
        }

        return result.stream()
                .map(eventMapper::toEventResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventResponseDto getPublishedEventById(Long eventId) {
        Event event = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED).orElseThrow(
                () -> new ObjectNotFoundException(EVENT_WAS_NOT_FOUND_MESSAGE, eventId));

        return addStatistics(eventMapper.toEventResponseDto(event), null, null);
    }

    @Override
    public List<EventResponseDto> getEventsByUserId(Long userId, int from, int size) {
        return eventRepository.findAllByInitiatorId(userId, CustomPageRequest.of(from, size))
                .stream()
                .map(eventMapper::toEventResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventResponseDto getEventByIdAndUserId(Long userId, Long eventId) {
        existUserById(userId);
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(
                () -> new ObjectNotFoundException(EVENT_WAS_NOT_FOUND_MESSAGE, eventId));

        return eventMapper.toEventResponseDto(event);
    }

    @Override
    @Transactional
    public EventResponseDto createEvent(Long userId, EventRequestDto eventRequestDto) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException(USER_WAS_NOT_FOUND_MESSAGE, userId));

        Event event = eventMapper.toEvent(eventRequestDto);
        event.setInitiator(user);
        event.setCreatedOn(LocalDateTime.now());
        setStateAndCountStatistics(event, EventState.PENDING);

        return eventMapper.toEventResponseDto(eventRepository.saveAndFlush(event));
    }

    @Override
    @Transactional
    public EventResponseDto updateEvent(Long userId, Long eventId, EventUpdateRequestDto eventUpdateRequestDto) {
        CustomValidator.validate(eventUpdateRequestDto);

        existUserById(userId);
        Event storedEvent = findEventById(eventId);

        if (storedEvent.getState().equals(EventState.PUBLISHED)) {
            throw new IllegalActionException("Changing requests after publishing is not allowed.");
        }

        Action stateAction = eventUpdateRequestDto.getStateAction();
        if (stateAction != null) {
            switch (getAction(eventUpdateRequestDto)) {
                case SEND_TO_REVIEW:
                    eventMapper.toEvent(eventUpdateRequestDto, storedEvent);
                    setStateAndCountStatistics(storedEvent, EventState.PENDING);
                    break;

                case REJECT_EVENT:
                case CANCEL_REVIEW:
                    setStateAndCountStatistics(storedEvent, EventState.CANCELED);
                    break;
            }
        }

        return eventMapper.toEventResponseDto(storedEvent);
    }

    @Override
    @Transactional
    public EventResponseDto handleEventPublication(Long eventId, EventUpdateRequestDto eventUpdateRequestDto) {
        CustomValidator.validate(eventUpdateRequestDto);

        Event storedEvent = findEventById(eventId);
        eventMapper.toEvent(eventUpdateRequestDto, storedEvent);

        Action stateAction = eventUpdateRequestDto.getStateAction();

        if (stateAction != null) {
            validateActionOnEvent(storedEvent.getState(), stateAction);

            switch (stateAction) {
                case PUBLISH_EVENT:
                    storedEvent.setReviewComment(null);
                    storedEvent.setPublishedOn(LocalDateTime.now());
                    setStateAndCountStatistics(storedEvent, EventState.PUBLISHED);
                    break;

                case REJECT_EVENT:
                    setStateAndCountStatistics(storedEvent, EventState.CANCELED);
                    storedEvent.setReviewComment(eventUpdateRequestDto.getReviewComment());
                    break;

                case SEND_EVENT_FOR_REVISION:
                    setStateAndCountStatistics(storedEvent, EventState.UNDER_REVISION);
                    storedEvent.setReviewComment(eventUpdateRequestDto.getReviewComment());
                    break;
            }
        }

        return eventMapper.toEventResponseDto(eventRepository.saveAndFlush(storedEvent));
    }

    @Override
    public Event findEventById(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(
                () -> new ObjectNotFoundException(EVENT_WAS_NOT_FOUND_MESSAGE, eventId));
    }

    @Override
    public List<EventStatisticsDto> getEventCountByState() {
        return eventStatisticsMapper.toEventStatisticsDto(eventStatisticsRepository.findAll());
    }

    private EventResponseDto addStatistics(EventResponseDto erd, LocalDateTime start, LocalDateTime end) {
        long confirmedRequests = requestRepository.countAllByEventIdAndStatus(erd.getId(), RequestStatus.CONFIRMED);
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

    private void existUserById(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException(USER_WAS_NOT_FOUND_MESSAGE, userId);
        }
    }

    private void validateActionOnEvent(EventState eventState, Action action) {
        if (eventState.equals(EventState.PUBLISHED) || eventState.equals(EventState.CANCELED)) {
            throw new IllegalActionException("Illegal action: " + action + " for a " + eventState + " event.");
        }
    }

    private void setStateAndCountStatistics(Event event, EventState newState) {
        EventState oldState = event.getState();
        if (oldState != null) {
            saveStatistics(oldState, -1);
        }

        saveStatistics(newState, 1);
        event.setState(newState);
    }

    private void saveStatistics(EventState eventState, long increment) {
        EventStatistics eventStatistics = eventStatisticsRepository.findById(eventState)
                .orElse(EventStatistics.builder()
                        .state(eventState)
                        .count(0L)
                        .build());

        eventStatistics.setCount(eventStatistics.getCount() + increment);
        eventStatisticsRepository.saveAndFlush(eventStatistics);
    }
}
