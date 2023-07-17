package ru.practicum.common;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class CustomPageRequest extends PageRequest {
    public CustomPageRequest(int from, int size, Sort sort) {
        super(from / size, size, sort);
    }

    public static CustomPageRequest of(int from, int size) {
        return of(from, size, Sort.unsorted());
    }

    public static CustomPageRequest of(int from, int size, Sort sort) {
        return new CustomPageRequest(from, size, sort);
    }
}
