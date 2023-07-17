package ru.practicum.service;

import ru.practicum.dto.HitRequestDto;
import ru.practicum.dto.HitResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public interface HitService {
    void saveHit(HitRequestDto hitRequestDto);

    List<HitResponseDto> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
