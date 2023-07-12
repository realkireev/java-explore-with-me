package ru.practicum.common;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.dto.ManuallyValidated;
import ru.practicum.exception.IllegalParametersException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class Validator {
    private final javax.validation.Validator validator;

    public void validate(ManuallyValidated eventUpdateRequestDto) {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(eventUpdateRequestDto);

        // Ignore NotBlank only
        constraintViolations.forEach(x -> {
            if (!x.getMessageTemplate().contains("NotBlank")) {
                throw new ConstraintViolationException(constraintViolations);
            }
        });
    }

    public void validateConsequentDates(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null && end.isBefore(start)) {
            throw new IllegalParametersException("rangeEnd must be after rangeStart.");
        }
    }
}
