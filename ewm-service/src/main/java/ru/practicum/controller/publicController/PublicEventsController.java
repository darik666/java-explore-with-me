package ru.practicum.controller.publicController;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.enums.Sort;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.service.Constants;
import ru.practicum.service.events.EventsService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Публичный контроллер событий
 */
@Valid
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events")
public class PublicEventsController {

    private final EventsService eventsService;

    /**
     * Получение событий с возможностью фильтрации
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getAll(
            @RequestParam(value = "text", defaultValue = "") String text,
            @RequestParam(value = "categories", required = false) List<Long> categories,
            @RequestParam(value = "paid", required = false) Boolean paid,
            @RequestParam(value = "rangeStart", defaultValue = Constants.DEFAULTSTARTTIME)
            @DateTimeFormat(pattern = Constants.DATETIMEFORMAT) LocalDateTime rangeStart,
            @RequestParam(value = "rangeEnd", defaultValue = Constants.DEFAULTENDTIME)
            @DateTimeFormat(pattern = Constants.DATETIMEFORMAT) LocalDateTime rangeEnd,
            @RequestParam(value = "onlyAvailable", required = false) Boolean onlyAvailable,
            @RequestParam(value = "sort", required = false) Sort sort,
            @RequestParam(defaultValue = "0") @Positive int from,
            @RequestParam(defaultValue = "10") @Positive int size,
            HttpServletRequest httpServletRequest) {
        if (rangeStart.isAfter(rangeEnd)) {
            throw new ValidationException("Дата окончания события не может быть раньше даты начала");
        }
        return eventsService.getAll(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, from, size, httpServletRequest);
    }

    /**
     * Получение подробной информации об опубликованном событии по его идентификатору
     */
    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto get(@PathVariable @Positive Long eventId,
                            HttpServletRequest httpServletRequest) {
        return eventsService.getById(eventId, httpServletRequest);
    }
}