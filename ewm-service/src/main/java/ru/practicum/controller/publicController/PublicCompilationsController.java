package ru.practicum.controller.publicController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.service.compilations.CompilationsService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

/**
 * Публичный контроллер подборок событий
 */
@Valid
@RestController
@RequestMapping(path = "/compilations")
public class PublicCompilationsController {

    private final CompilationsService compilationsService;

    @Autowired
    public PublicCompilationsController(CompilationsService compilationsService) {
        this.compilationsService = compilationsService;
    }

    /**
     * Получение списка подборок событий
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CompilationDto> getCompilations(@RequestParam (defaultValue = "false") boolean pinned,
                                                @RequestParam(defaultValue = "0") Integer from,
                                                @RequestParam(defaultValue = "10") Integer size) {
        return compilationsService.getCompilations(pinned, from, size);
    }

    /**
     * Получение подборки событий по id
     */
    @GetMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto get(@PathVariable @Positive Long compId,
                              HttpServletRequest httpServletRequest) {
        return compilationsService.getById(compId, httpServletRequest);
    }
}