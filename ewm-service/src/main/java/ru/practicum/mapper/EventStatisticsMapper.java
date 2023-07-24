package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.dto.EventStatisticsDto;
import ru.practicum.model.EventStatistics;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventStatisticsMapper {
    List<EventStatisticsDto> toEventStatisticsDto(List<EventStatistics> eventStatistics);
}
