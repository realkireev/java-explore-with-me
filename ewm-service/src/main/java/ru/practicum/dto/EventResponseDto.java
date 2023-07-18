package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.model.Category;
import ru.practicum.model.EventState;
import ru.practicum.model.Location;
import ru.practicum.model.User;

import java.time.LocalDateTime;

import static ru.practicum.common.Variables.GLOBAL_DATE_PATTERN;

@Getter
@Setter
public class EventResponseDto {
    private String annotation;
    private Category category;
    private Long confirmedRequests;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GLOBAL_DATE_PATTERN)
    private LocalDateTime createdOn;
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GLOBAL_DATE_PATTERN)
    private LocalDateTime eventDate;
    private Long id;
    private User initiator;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GLOBAL_DATE_PATTERN)
    private LocalDateTime publishedOn;
    private Boolean requestModeration;
    private EventState state;
    private String title;
    private Long views;
}
