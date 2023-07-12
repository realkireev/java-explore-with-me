package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.HitRequestDto;
import ru.practicum.dto.HitResponseDto;
import ru.practicum.mapper.HitMapper;
import ru.practicum.repo.HitRepository;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HitService {
    private final HitRepository hitRepository;
    private final HitMapper hitMapper;

    public void saveHit(HitRequestDto hitRequestDto) {
        hitRepository.saveAndFlush(hitMapper.toHit(hitRequestDto));
    }

    public List<HitResponseDto> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris,
                                              Boolean unique) {
        List<HitResponseDto> result;
        List<String> clearedUris = uris.stream()
                .map(x -> x.replace("[", "").replace("]", ""))
                .collect(Collectors.toList());

        if (!unique) {
            if (uris.size() == 0) {
                result = hitRepository.countAllBetween(start, end).stream()
                        .map(hitMapper::toHitResponseDto).collect(Collectors.toList());
            } else {
                result = hitRepository.countAllByUrisBetween(start, end, clearedUris).stream()
                        .map(hitMapper::toHitResponseDto).collect(Collectors.toList());
            }
        } else {
            if (uris.size() == 0) {
                result = toHitResponseDto(hitRepository.countUniqueBetween(start, end));
            } else {
                result = toHitResponseDto(hitRepository.countUniqueByUrisBetween(start, end, clearedUris));
            }
        }

        return result;
    }

    private List<HitResponseDto> toHitResponseDto(List<Object[]> result) {
        List<HitResponseDto> hitResponseDtoList = new ArrayList<>();

        result.forEach(x -> {
            HitResponseDto hitResponseDto = new HitResponseDto((String) x[0], (String) x[1],
                    ((BigInteger) x[2]).longValue());
            hitResponseDtoList.add(hitResponseDto);
        });

        return hitResponseDtoList;
    }
}
