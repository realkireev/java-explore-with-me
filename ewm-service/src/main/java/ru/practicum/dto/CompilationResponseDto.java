package ru.practicum.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.model.Event;

import java.util.Set;

@Getter
@Setter
public class CompilationResponseDto {
    private Long id;
    private String title;
    private Boolean pinned;
    private Set<Event> events;
}
