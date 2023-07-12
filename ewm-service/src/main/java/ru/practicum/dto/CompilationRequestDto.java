package ru.practicum.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.Set;

@Getter
@Setter
public class CompilationRequestDto {
    @NotBlank
    @Length(min = 1, max = 50)
    private String title;

    private Boolean pinned;
    private Set<Long> events;
}
