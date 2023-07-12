package ru.practicum.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.client.StatisticsClient;
import ru.practicum.dto.HitResponseDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.common.Variables.APP_NAME;
import static ru.practicum.common.Variables.DEFAULT_END_DATE;
import static ru.practicum.common.Variables.DEFAULT_START_DATE;
import static ru.practicum.common.Variables.GLOBAL_DATE_FORMATTER;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    private final StatisticsClient statisticsClient;

    public void saveStatistics(HttpServletRequest request) throws JsonProcessingException {
        statisticsClient.saveStatistics(APP_NAME, request.getRequestURI(), request.getRemoteAddr());
    }

    public List<HitResponseDto> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        String startString;
        String endString;

        if (start == null) {
            startString = DEFAULT_START_DATE.format(GLOBAL_DATE_FORMATTER);
        } else {
            startString = start.format(GLOBAL_DATE_FORMATTER);
        }

        if (end == null) {
            endString = DEFAULT_END_DATE.format(GLOBAL_DATE_FORMATTER);
        } else {
            endString = end.format(GLOBAL_DATE_FORMATTER);
        }

        List<HitResponseDto> result = statisticsClient.getStatistics(startString, endString, uris, unique).getBody();

        if (result != null && result.size() > 0) {
            return result;
        } else {
            return null;
        }
    }
}
