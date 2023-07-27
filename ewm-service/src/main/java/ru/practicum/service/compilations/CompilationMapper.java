package ru.practicum.service.compilations;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.model.Compilation;

import java.time.LocalDateTime;

/**
 * Маппер подборок
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Component
public class CompilationMapper {

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
        return dto;
    }
}