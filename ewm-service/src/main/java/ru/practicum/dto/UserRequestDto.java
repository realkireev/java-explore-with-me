package ru.practicum.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class UserRequestDto {
    @Email
    @NotNull
    @Length(min = 6, max = 254)
    String email;

    @NotNull
    @NotBlank
    @Length(min = 2, max = 250)
    String name;
}
