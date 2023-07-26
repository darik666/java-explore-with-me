package ru.practicum.service.compilations;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.EwmClient;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationRequest;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Compilation;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompilationsServiceImpl implements CompilationsService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;
    private final EwmClient ewmClient;

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return compilationRepository.findAllByPinned(pinned, pageable).stream()
                .map(compilationMapper::toCompilationDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getById(Long id, HttpServletRequest httpServletRequest) {
        Compilation compilation = compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Подборка с id=%d не найдена", id)));
        ewmClient.addHit(httpServletRequest);
        return compilationMapper.toCompilationDto(compilation);
    }

    @Transactional
    @Override
    public CompilationDto create(NewCompilationDto newDto) {
        Compilation compilation = compilationMapper.toCompilationFromNew(newDto);
        if (newDto.getEvents() != null) {
            compilation.setEvents(eventRepository.findAllById(newDto.getEvents()));
        }
        return compilationMapper.toCompilationDto(compilationRepository.save(compilation));
    }

    @Transactional
    @Override
    public void delete(Long compId) {
        compilationRepository.deleteById(compId);
    }

    @Transactional
    @Override
    public CompilationDto update(UpdateCompilationRequest dto, Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(String.format("Подборка с id=%d не найдена", compId)));
        compilation.setTitle(dto.getTitle());
        compilation.setPinned(dto.isPinned());
        if (dto.getEvents() != null && !dto.getEvents().isEmpty()) {
            compilation.setEvents(eventRepository.findAllById(dto.getEvents()));
        }
        Compilation updatedCompilation = compilationRepository.save(compilation);
        return compilationMapper.toCompilationDto(updatedCompilation);
    }
}