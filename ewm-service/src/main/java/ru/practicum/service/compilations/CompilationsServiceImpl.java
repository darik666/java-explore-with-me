package ru.practicum.service.compilations;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.EwmClient;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.service.events.EventMapper;
import ru.practicum.service.events.EventsService;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервисный класс подборок событий
 */
@Slf4j
@Service
@AllArgsConstructor
public class CompilationsServiceImpl implements CompilationsService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;
    private final EventMapper eventMapper;
    private final EventsService eventsService;
    private final EwmClient ewmClient;

    /**
     * Получение списка подборок событий
     */
    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);
        List<CompilationDto> compilationDtos = compilationRepository.findAllByPinned(pinned, pageable)
                .stream()
                .map(compilationMapper::toCompilationDto)
                .collect(Collectors.toList());

        compilationDtos.forEach(comp -> {
            List<Long> eventIds = eventRepository.findEventIdsByCompilationId(comp.getId());
            List<Event> events = eventRepository.getEventsByIdList(eventIds);
            List<EventShortDto> eventShortDtos = eventsService.getEventShortDtoList(events);
            comp.setEvents(eventShortDtos);
        });
        log.debug("Получение списка подборок событий", compilationDtos);
        return compilationDtos;
    }

    /**
     * Получение подборки событий по id
     */
    @Override
    public CompilationDto getById(Long id, HttpServletRequest httpServletRequest) {
        Compilation compilation = compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Подборка с id=%d не найдена", id)));
        ewmClient.addHit(httpServletRequest);
        List<Event> events = compilation.getEvents();
        CompilationDto dto = compilationMapper.toCompilationDto(compilation);
        dto.setEvents(eventsService.getEventShortDtoList(events));
        log.debug("Получение подборки событий: ", dto);
        return dto;
    }

    /**
     * Создание подборки событий
     */
    @Transactional
    @Override
    public CompilationDto create(NewCompilationDto newDto) {
        Compilation compilation = compilationMapper.toCompilationFromNew(newDto);
        List<Event> events = new ArrayList<>();
        if (newDto.getEvents() != null) {
            events = eventRepository.findAllById(newDto.getEvents());
            compilation.setEvents(events);
        }
        Compilation comp = compilationRepository.save(compilation);
        CompilationDto dto = compilationMapper.toCompilationDto(comp);
        dto.setEvents(eventsService.getEventShortDtoList(events));
        log.debug("Создание подборки событий: ", dto);
        return dto;
    }

    /**
     * Удаление подборки событий
     */
    @Transactional
    @Override
    public void delete(Long compId) {
        log.debug("Удаление подборки событий id=: ", compId);
        compilationRepository.deleteById(compId);
    }

    /**
     * Обновление подборки событий
     */
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
        List<Event> events = updatedCompilation.getEvents();
        CompilationDto compDto = compilationMapper.toCompilationDto(updatedCompilation);
        compDto.setEvents(eventsService.getEventShortDtoList(events));
        log.debug("Обновление подборки событий: ", compDto);
        return compDto;
    }
}