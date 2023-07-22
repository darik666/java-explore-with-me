package ru.practicum.controller.adminController;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationRequest;
import ru.practicum.service.compilations.CompilationsService;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/admin/compilations")
public class AdminCompilationsController {
    private final CompilationsService service;

    @PostMapping
    public CompilationDto createCompilation(@RequestBody NewCompilationDto dto) {
        return service.create(dto);
    }

    @DeleteMapping("/{compId}")
    public void deleteCompilation(@PathVariable Long compId) {
        service.delete(compId);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(@PathVariable Long compId,
                                            @RequestBody UpdateCompilationRequest dto) {
        return service.update(dto, compId);
    }
}
