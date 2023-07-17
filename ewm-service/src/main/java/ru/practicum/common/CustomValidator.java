package ru.practicum.common;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.dto.ManuallyValidated;
import ru.practicum.exception.IllegalParametersException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class CustomValidator {
    private static final Validator CUSTOM_VALIDATOR;

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        CUSTOM_VALIDATOR = factory.getValidator();
    }


    public static void validate(ManuallyValidated eventUpdateRequestDto) {
        Set<ConstraintViolation<Object>> constraintViolations = CUSTOM_VALIDATOR.validate(eventUpdateRequestDto);

        // Ignore NotBlank only
        constraintViolations.forEach(x -> {
            if (!x.getMessageTemplate().contains("NotBlank")) {
                throw new ConstraintViolationException(constraintViolations);
            }
        });
    }

    public static void validateConsequentDates(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null && end.isBefore(start)) {
            throw new IllegalParametersException("rangeEnd must be after rangeStart.");
        }
    }
}
