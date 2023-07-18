package ru.practicum.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.model.Action;

@Getter
@Setter
public class EventUpdateRequestDto extends EventRequestDto implements ManuallyValidated {
    private Action stateAction;
}
