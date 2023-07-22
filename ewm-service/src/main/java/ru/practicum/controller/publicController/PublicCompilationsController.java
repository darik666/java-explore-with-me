package ru.practicum.controller.publicController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CompilationDto;
import ru.practicum.service.compilations.CompilationsService;

import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping(path = "/compilations")
public class PublicCompilationsController {
    private final CompilationsService compilationsService;

    @Autowired
    public PublicCompilationsController(CompilationsService compilationsService) {
        this.compilationsService = compilationsService;
    }

    @GetMapping
    public List<CompilationDto> getCompilations(@RequestParam (defaultValue = "false") boolean pinned,
                                                @RequestParam(defaultValue = "0") Integer from,
                                                @RequestParam(defaultValue = "10") Integer size) {
        return compilationsService.getCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto get(@PathVariable @Positive Long id) {
        return compilationsService.getById(id);
    }
}