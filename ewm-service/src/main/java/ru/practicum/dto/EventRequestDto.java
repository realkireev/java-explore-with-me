package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import ru.practicum.model.Location;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

import static ru.practicum.common.Variables.GLOBAL_DATE_PATTERN;

@Getter
@Setter
public class EventRequestDto {
    @NotBlank
    @Length(min = 20, max = 2000)
    private String annotation;
    private Long category;

    @NotBlank
    @Length(min = 20, max = 7000)
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GLOBAL_DATE_PATTERN)
    @FutureOrPresent
    private LocalDateTime eventDate;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;

    @Length(min = 3, max = 120)
    private String title;
}
