package ru.practicum.controller.errorhandler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exception.IllegalActionException;
import ru.practicum.exception.IllegalParametersException;
import ru.practicum.exception.ObjectNotFoundException;

import javax.validation.ConstraintViolationException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

import static ru.practicum.common.Variables.BAD_REQUEST_REASON;
import static ru.practicum.common.Variables.CONFLICT_REASON;
import static ru.practicum.common.Variables.GLOBAL_DATE_PATTERN;
import static ru.practicum.common.Variables.NOT_FOUND_REASON;

@RestControllerAdvice
public class AllControllersErrorHandler {
    @ExceptionHandler({
            SQLException.class,
            IllegalActionException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleSQLException(final Exception e) {
        return createResponseObject(HttpStatus.CONFLICT.toString(), CONFLICT_REASON, e.getMessage());
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            NumberFormatException.class,
            ConstraintViolationException.class,
            UnsupportedEncodingException.class,
            IllegalParametersException.class,
            MissingServletRequestParameterException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBadRequest(final Exception e) {
        return createResponseObject(HttpStatus.BAD_REQUEST.toString(), BAD_REQUEST_REASON, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(final ObjectNotFoundException e) {
        return createResponseObject(HttpStatus.NOT_FOUND.toString(), NOT_FOUND_REASON, e.getMessage());
    }

    private Map<String, String> createResponseObject(String status, String reason, String message) {
        Map<String, String> result = new LinkedHashMap<>();
        result.put("status", status);
        result.put("reason", reason);
        result.put("message", message);
        result.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern(GLOBAL_DATE_PATTERN)));

        return result;
    }
}
