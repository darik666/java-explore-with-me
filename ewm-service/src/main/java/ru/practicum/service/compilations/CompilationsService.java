package ru.practicum.service.compilations;

import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface CompilationsService {
    List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto getById(Long id, HttpServletRequest httpServletRequest);

    CompilationDto create(NewCompilationDto newDto);

    void delete(Long compId);

    CompilationDto update(UpdateCompilationRequest dto, Long compId);
}
