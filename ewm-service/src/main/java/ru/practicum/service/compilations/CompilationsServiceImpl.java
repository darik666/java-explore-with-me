package ru.practicum.service.compilations;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationRequest;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Compilation;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompilationsServiceImpl implements CompilationsService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return compilationRepository.findAllByPinned(pinned, pageable).stream().map(CompilationMapper::toCompilationDto).collect(Collectors.toList());
    }

    @Override
    public CompilationDto getById(Long id) {
        Compilation compilation = compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Подборка с id=%d не найдена", id)));
        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    public CompilationDto create(NewCompilationDto newDto) {
        Compilation compilation = CompilationMapper.toCompilationFromNew(newDto);
        if (newDto.getEvents() != null && newDto.getEvents().size() > 0) {
            compilation.setEvents(eventRepository.findAllById(newDto.getEvents()));
        }
        return CompilationMapper.toCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    public void delete(Long compId) {
        compilationRepository.deleteById(compId);
    }

    @Override
    public CompilationDto update(UpdateCompilationRequest dto, Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(String.format("Подборка с id=%d не найдена", compId)));
        compilation.setTitle(dto.getTitle());
        compilation.setPinned(dto.isPinned());
        if (dto.getEvents() != null && !dto.getEvents().isEmpty()) {
            compilation.setEvents(eventRepository.findAllById(dto.getEvents()));
        } else {
            compilation.getEvents().clear();
        }
        Compilation updatedCompilation = compilationRepository.save(compilation);
        return CompilationMapper.toCompilationDto(updatedCompilation);
    }
}