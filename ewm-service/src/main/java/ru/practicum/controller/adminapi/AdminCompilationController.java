package ru.practicum.controller.adminapi;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.CompilationRequestDto;
import ru.practicum.dto.CompilationResponseDto;
import ru.practicum.dto.CompilationUpdateRequestDto;
import ru.practicum.service.CompilationService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping(path = "/admin/compilations")
@Validated
@RequiredArgsConstructor
public class AdminCompilationController {
    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationResponseDto createCompilation(@Valid @NotNull @RequestBody CompilationRequestDto compRequestDto) {
        return compilationService.createCompilation(compRequestDto);
    }

    @PatchMapping(path = "/{compilationId}")
    public CompilationResponseDto updateCompilation(@PathVariable Long compilationId,
                                              @RequestBody CompilationUpdateRequestDto compilationUpdateRequestDto) {
        return compilationService.updateCompilation(compilationId, compilationUpdateRequestDto);
    }

    @DeleteMapping(path = "/{compilationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compilationId) {
        compilationService.deleteCompilation(compilationId);
    }
}
