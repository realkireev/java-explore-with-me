package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.dto.EventRequestDto;
import ru.practicum.dto.EventResponseDto;
import ru.practicum.dto.EventUpdateRequestDto;
import ru.practicum.model.Event;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = "spring")
public interface EventMapper {
    @Mapping(source = "lat", target = "location.lat")
    @Mapping(source = "lon", target = "location.lon")
    EventResponseDto toEventResponseDto(Event event);

    @Mapping(source = "category", target = "category.id")
    @Mapping(source = "eventRequestDto.location.lat", target = "lat")
    @Mapping(source = "eventRequestDto.location.lon", target = "lon")
    @Mapping(source = "paid", target = "paid", defaultValue = "false")
    @Mapping(source = "requestModeration", target = "requestModeration", defaultValue = "true")
    @Mapping(source = "participantLimit", target = "participantLimit", defaultValue = "0")
    Event toEvent(EventRequestDto eventRequestDto);

    @Mapping(source = "eventUpdateRequestDto.title", target = "event.title")
    @Mapping(source = "eventUpdateRequestDto.description", target = "event.description")
    @Mapping(source = "eventUpdateRequestDto.annotation", target = "event.annotation")
    @Mapping(source = "eventUpdateRequestDto.category", target = "event.category.id")
    @Mapping(source = "eventUpdateRequestDto.eventDate", target = "event.eventDate")
    @Mapping(source = "eventUpdateRequestDto.location.lat", target = "event.lat")
    @Mapping(source = "eventUpdateRequestDto.location.lon", target = "event.lon")
    @Mapping(source = "eventUpdateRequestDto.paid", target = "event.paid")
    @Mapping(source = "eventUpdateRequestDto.participantLimit", target = "event.participantLimit")
    @Mapping(source = "eventUpdateRequestDto.requestModeration", target = "event.requestModeration")
    @Mapping(source = "eventUpdateRequestDto.reviewComment", target = "event.reviewComment")
    void toEvent(EventUpdateRequestDto eventUpdateRequestDto, @MappingTarget Event event);
}
