package ru.practicum.service.compilations;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.model.Compilation;
import ru.practicum.service.events.EventMapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Маппер подборок
 */
@Component
@AllArgsConstructor
public class CompilationMapper {

    private EventMapper eventMapper;

    public Compilation toCompilationFromNew(NewCompilationDto dto) {
        Compilation comp = new Compilation();
        comp.setPinned(dto.getPinned());
        comp.setTitle(dto.getTitle());
        comp.setCreated(LocalDateTime.now());
        return comp;
    }

    public CompilationDto toCompilationDto(Compilation comp) {
        CompilationDto dto = new CompilationDto();
        dto.setId(comp.getId());
        dto.setPinned(comp.getPinned());
        dto.setTitle(comp.getTitle());
        if (comp.getEvents() != null) {
            List<EventShortDto> shorts = eventMapper.getEventShortDtoList(comp.getEvents());
            dto.setEvents(shorts);
        }
        return dto;
    }
}