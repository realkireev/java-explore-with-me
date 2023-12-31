package ru.practicum.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Variables {
    public static final String GLOBAL_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter GLOBAL_DATE_FORMATTER = DateTimeFormatter.ofPattern(GLOBAL_DATE_PATTERN);
    public static final LocalDateTime DEFAULT_START_DATE = LocalDateTime.parse("1900-01-01 00:00:00",
            GLOBAL_DATE_FORMATTER);
    public static final LocalDateTime DEFAULT_END_DATE = LocalDateTime.parse("9999-01-01 00:00:00",
        GLOBAL_DATE_FORMATTER);
    public static final String APP_NAME = "ewm-main-service";

    public static final String FROM_DEFAULT = "0";
    public static final String SIZE_DEFAULT = "10";
    public static final String FROM_BELOW_ZERO_MESSAGE = "From should be >= 0";
    public static final String SIZE_NOT_POSITIVE_MESSAGE = "Size should be >= 0";
    public static final String BAD_REQUEST_REASON = "Incorrectly made request.";
    public static final String CONFLICT_REASON = "Integrity constraint has been violated.";
    public static final String NOT_FOUND_REASON = "The required object was not found.";
    public static final String INTERNAL_SERVER_ERROR_REASON = "Internal server error was encountered.";
    public static final String USER_WAS_NOT_FOUND_MESSAGE = "User with id=%d was not found";
    public static final String CATEGORY_WAS_NOT_FOUND_MESSAGE = "Category with id=%d was not found";
    public static final String EVENT_WAS_NOT_FOUND_MESSAGE = "Event with id=%d was not found.";
    public static final String COMPILATION_WAS_NOT_FOUND_MESSAGE = "Compilation with id=%d was not found";
    public static final String REQUEST_WAS_NOT_FOUND_MESSAGE = "Request with id=%d was not found";
}
