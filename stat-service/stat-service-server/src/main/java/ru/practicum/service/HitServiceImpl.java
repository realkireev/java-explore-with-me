package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.HitRequestDto;
import ru.practicum.dto.HitResponseDto;
import ru.practicum.exception.IllegalParametersException;
import ru.practicum.mapper.HitMapper;
import ru.practicum.repo.HitRepository;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HitServiceImpl implements HitService {
    private final HitRepository hitRepository;
    private final HitMapper hitMapper;

    @Override
    public void saveHit(HitRequestDto hitRequestDto) {
        hitRepository.saveAndFlush(hitMapper.toHit(hitRequestDto));
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<HitResponseDto> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris,
                                              Boolean unique) {
        validateConsequentDates(start, end);

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

    private void validateConsequentDates(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null && end.isBefore(start)) {
            throw new IllegalParametersException("End must be after start.");
        }
    }
}
