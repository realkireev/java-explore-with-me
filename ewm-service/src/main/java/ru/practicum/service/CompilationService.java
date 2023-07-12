package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.common.Validator;
import ru.practicum.dto.CompilationRequestDto;
import ru.practicum.dto.CompilationResponseDto;
import ru.practicum.dto.CompilationUpdateRequestDto;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.model.Compilation;
import ru.practicum.repo.CompilationRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final Validator validator;

    public List<CompilationResponseDto> getCompilations(Boolean pinned, int from, int size) {
        return compilationRepository.findAll(PageRequest.of(from / size, size)).stream()
                .map(compilationMapper::toCompilationResponseDto)
                .collect(Collectors.toList());
    }

    public CompilationResponseDto getCompilationById(Long compilationId) {
        return compilationMapper.toCompilationResponseDto(findCompilationById(compilationId));
    }

    public void deleteCompilation(Long compilationId) {
        existsCompilationById(compilationId);
        compilationRepository.deleteById(compilationId);
    }

    public CompilationResponseDto updateCompilation(Long compilationId, CompilationUpdateRequestDto compUpdateRequestDto) {
        validator.validate(compUpdateRequestDto);

        Compilation compilation = findCompilationById(compilationId);
        compilationMapper.toCompilation(compUpdateRequestDto, compilation);

        return compilationMapper.toCompilationResponseDto(compilationRepository.saveAndFlush(compilation));
    }

    public CompilationResponseDto createCompilation(CompilationRequestDto compilationRequestDto) {
        Compilation compilation = compilationMapper.toCompilation(compilationRequestDto);

        return compilationMapper.toCompilationResponseDto(compilationRepository.saveAndFlush(compilation));
    }

    private Compilation findCompilationById(Long compilationId) {
        Optional<Compilation> result = compilationRepository.findById(compilationId);

        if (result.isEmpty()) {
            throwObjectNotFoundException(compilationId);
        }

        return result.get();
    }

    private void existsCompilationById(Long compilationId) {
        if (!compilationRepository.existsById(compilationId)) {
            throwObjectNotFoundException(compilationId);
        }
    }

    private void throwObjectNotFoundException(Long compilationId) {
        throw new ObjectNotFoundException("Compilation with id=%d was not found", compilationId);
    }
}
