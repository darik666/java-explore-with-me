package ru.practicum.controller.publicController;

import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.Sort;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.service.events.EventsService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/events")
public class PublicEventsController {

    EventsService eventsService;

    @GetMapping
    public List<EventShortDto> getAll(
            @RequestParam(value = "text", required = false, defaultValue = "") String text,
            @RequestParam(value = "categories", required = false) List<Long> categories,
            @RequestParam(value = "paid", required = false) Boolean paid,
            @RequestParam(value = "rangeStart", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(value = "rangeEnd", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(value = "onlyAvailable", required = false) Boolean onlyAvailable,
            @RequestParam(value = "sort") Sort sort,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {

        return eventsService.getAll(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto get(@PathVariable @Positive Long id,
                            HttpServletRequest httpServletRequest) {
        return eventsService.getById(id, httpServletRequest);
    }
}
