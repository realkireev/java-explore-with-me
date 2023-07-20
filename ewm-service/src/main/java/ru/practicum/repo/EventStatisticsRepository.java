package ru.practicum.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.EventState;
import ru.practicum.model.EventStatistics;

public interface EventStatisticsRepository extends JpaRepository<EventStatistics, EventState> {
}
