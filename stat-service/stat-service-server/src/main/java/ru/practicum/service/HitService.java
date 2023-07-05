package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.mapper.HitMapper;
import ru.practicum.repo.HitRepository;
import ru.practicum.dto.HitRequestDto;
import ru.practicum.dto.HitResponseDto;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HitService {
    private final HitRepository hitRepository;
    private final HitMapper hitMapper;

    public void saveHit(HitRequestDto hitRequestDto) {
        hitRepository.save(hitMapper.toHit(hitRequestDto));
    }

    public List<HitResponseDto> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris,
                                              Boolean unique) {
        if (!unique) {
            if (uris.size() == 0) {
                return hitRepository.countAllBetween(start, end);
            } else {
                return hitRepository.countAllByUrisBetween(start, end, uris);
            }
        } else {
            if (uris.size() == 0) {
                return toHitResponseDto(hitRepository.countUniqueBetween(start, end));
            } else {
                return toHitResponseDto(hitRepository.countUniqueByUrisBetween(start, end, uris));
            }
        }
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
