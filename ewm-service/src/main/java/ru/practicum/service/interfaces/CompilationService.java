package ru.practicum.service.interfaces;

import ru.practicum.dto.CompilationRequestDto;
import ru.practicum.dto.CompilationResponseDto;
import ru.practicum.dto.CompilationUpdateRequestDto;

import java.util.List;

public interface CompilationService {
    List<CompilationResponseDto> getCompilations(Boolean pinned, int from, int size);

    CompilationResponseDto getCompilationById(Long compilationId);

    void deleteCompilation(Long compilationId);

    CompilationResponseDto updateCompilation(Long compilationId, CompilationUpdateRequestDto compUpdateRequestDto);

    CompilationResponseDto createCompilation(CompilationRequestDto compilationRequestDto);
}
