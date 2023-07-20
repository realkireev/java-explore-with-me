package ru.practicum.repo;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.model.Event;
import ru.practicum.model.EventState;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {
    List<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long userId);

    Optional<Event> findByIdAndState(Long eventId, EventState state);

    @Query(value = "SELECT a.id FROM (" +
            " SELECT id, participant_limit, q.qty, (participant_limit = 0 OR participant_limit > q.qty OR q.qty ISNULL)" +
            " AS available" +
            " FROM events e" +
            " LEFT JOIN (SELECT event_id, COUNT(*) qty FROM request r " +
            " WHERE r.status = 'CONFIRMED'" +
            " GROUP BY r.event_id) AS q" +
            " ON q.event_id = e.id" +
            " ) AS a" +
            " WHERE a.available", nativeQuery = true)
    List<Long> findAvailableEventIds();
}
