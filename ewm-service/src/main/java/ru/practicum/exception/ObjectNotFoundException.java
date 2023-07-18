package ru.practicum.exception;

public class ObjectNotFoundException extends RuntimeException {
    public ObjectNotFoundException(String message, Long id) {
        super(String.format(message, id));
    }
}
