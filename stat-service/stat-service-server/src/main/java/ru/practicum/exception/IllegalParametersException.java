package ru.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IllegalParametersException extends RuntimeException {
    public IllegalParametersException(String message) {
        super(String.format(message));
    }
}
