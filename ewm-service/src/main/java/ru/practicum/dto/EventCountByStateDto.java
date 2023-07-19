package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.model.EventState;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventCountByStateDto {
    private EventState state;
    private Long count;
}
