package ru.practicum.service.compilations;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.model.Compilation;
import ru.practicum.service.EventMapper;

import java.time.LocalDateTime;

@Component
@AllArgsConstructor
public class CompilationMapper {
    private static EventMapper eventMapper;

    public static Compilation toCompilationFromNew(NewCompilationDto dto) {
        Compilation comp = new Compilation();
        comp.setPinned(dto.getPinned());
        comp.setTitle(dto.getTitle());
        comp.setCreated(LocalDateTime.now());
        return comp;
    }

    public static CompilationDto toCompilationDto(Compilation comp) {
        CompilationDto dto = new CompilationDto();
        dto.setId(comp.getId());
        dto.setPinned(comp.getPinned());
        dto.setTitle(comp.getTitle());
        if (comp.getEvents() != null && comp.getEvents().size() > 0) {
            dto.setEvents(eventMapper.getEventShortDtoList(comp.getEvents()));
        }
        return dto;
    }
}
