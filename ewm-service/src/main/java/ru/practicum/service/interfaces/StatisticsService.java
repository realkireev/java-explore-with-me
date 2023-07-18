package ru.practicum.service.interfaces;

import com.fasterxml.jackson.core.JsonProcessingException;
import ru.practicum.dto.HitResponseDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface StatisticsService {
    void saveStatistics(HttpServletRequest request) throws JsonProcessingException;

    List<HitResponseDto> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
