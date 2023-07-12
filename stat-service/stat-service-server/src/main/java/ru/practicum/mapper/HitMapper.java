package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.dto.HitAggregatedDto;
import ru.practicum.dto.HitRequestDto;
import ru.practicum.dto.HitResponseDto;
import ru.practicum.model.Hit;

@Mapper(componentModel = "spring")
public interface HitMapper {
    Hit toHit(HitRequestDto hitRequestDto);

    HitResponseDto toHitResponseDto(HitAggregatedDto hitAggregatedDto);
}
