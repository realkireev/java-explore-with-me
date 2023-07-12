package ru.practicum.mapper;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.dto.CompilationRequestDto;
import ru.practicum.dto.CompilationResponseDto;
import ru.practicum.dto.CompilationUpdateRequestDto;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;

import java.util.Set;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = "spring")
public interface CompilationMapper {
    @Mapping(source = "event", target = "events")
    CompilationResponseDto toCompilationResponseDto(Compilation compilation);

    default Event mapIdToEvent(Long id) {
        Event event = new Event();
        event.setId(id);

        return event;
    }

    @IterableMapping(elementTargetType = Event.class)
    Set<Event> mapEventIdsToEventSet(Set<Long> eventIds);

    @Mapping(source = "pinned", target = "pinned", defaultValue = "false")
    @Mapping(source = "events", target = "event")
    Compilation toCompilation(CompilationRequestDto compilationRequestDto);

    @Mapping(source = "compilationUpdateRequestDto.title", target = "compilation.title")
    @Mapping(source = "compilationUpdateRequestDto.pinned", target = "compilation.pinned")
    @Mapping(source = "compilationUpdateRequestDto.events", target = "compilation.event")
    void toCompilation(CompilationUpdateRequestDto compilationUpdateRequestDto, @MappingTarget Compilation compilation);
}
