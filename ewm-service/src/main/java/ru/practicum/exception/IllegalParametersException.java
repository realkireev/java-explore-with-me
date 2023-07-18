package ru.practicum.exception;

public class IllegalParametersException extends RuntimeException {
    public IllegalParametersException(String message) {
        super(String.format(message));
    }
}
