package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.RequestResponseDto;
import ru.practicum.model.Request;

@Mapper(componentModel = "spring")
public interface RequestMapper {
    @Mapping(source = "eventId", target = "event")
    @Mapping(source = "requesterId", target = "requester")
    RequestResponseDto toRequestResponseDto(Request request);
}
