package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.model.RequestStatus;

import java.time.LocalDateTime;

import static ru.practicum.common.Variables.GLOBAL_DATE_PATTERN;

@Getter
@Setter
public class RequestResponseDto {
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GLOBAL_DATE_PATTERN)
    private LocalDateTime created;

    private Long event;
    private Long requester;
    private RequestStatus status;
}
