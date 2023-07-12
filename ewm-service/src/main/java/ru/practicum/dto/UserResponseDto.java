package ru.practicum.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDto {
    Long id;
    String email;
    String name;
}
