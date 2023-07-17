package ru.practicum.service.implementations;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.common.CustomPageRequest;
import ru.practicum.common.CustomValidator;
import ru.practicum.dto.CompilationRequestDto;
import ru.practicum.dto.CompilationResponseDto;
import ru.practicum.dto.CompilationUpdateRequestDto;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.model.Compilation;
import ru.practicum.repo.CompilationRepository;
import ru.practicum.service.interfaces.CompilationService;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.common.Variables.COMPILATION_WAS_NOT_FOUND_MESSAGE;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;

    @Override
    public List<CompilationResponseDto> getCompilations(Boolean pinned, int from, int size) {
        return compilationRepository.findAll(CustomPageRequest.of(from, size)).stream()
                .map(compilationMapper::toCompilationResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationResponseDto getCompilationById(Long compilationId) {
        return compilationMapper.toCompilationResponseDto(findCompilationById(compilationId));
    }

    @Override
    public void deleteCompilation(Long compilationId) {
        existsCompilationById(compilationId);
        compilationRepository.deleteById(compilationId);
    }

    @Override
    public CompilationResponseDto updateCompilation(Long compilationId, CompilationUpdateRequestDto compUpdateRequestDto) {
        CustomValidator.validate(compUpdateRequestDto);

        Compilation compilation = findCompilationById(compilationId);
        compilationMapper.toCompilation(compUpdateRequestDto, compilation);

        return compilationMapper.toCompilationResponseDto(compilationRepository.saveAndFlush(compilation));
    }

    @Override
    public CompilationResponseDto createCompilation(CompilationRequestDto compilationRequestDto) {
        Compilation compilation = compilationMapper.toCompilation(compilationRequestDto);

        return compilationMapper.toCompilationResponseDto(compilationRepository.saveAndFlush(compilation));
    }

    private Compilation findCompilationById(Long compilationId) {
        return compilationRepository.findById(compilationId).orElseThrow(
                () -> new ObjectNotFoundException(COMPILATION_WAS_NOT_FOUND_MESSAGE, compilationId));
    }

    private void existsCompilationById(Long compilationId) {
        if (!compilationRepository.existsById(compilationId)) {
            throw new ObjectNotFoundException(COMPILATION_WAS_NOT_FOUND_MESSAGE, compilationId);
        }
    }
}
