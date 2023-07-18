package ru.practicum.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class CategoryRequestDto {
    @Length(min = 1, max = 50)
    @NotBlank
    private String name;
}
