package ru.practicum.controller.adminController;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.model.enums.State;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.service.Constants;
import ru.practicum.service.events.EventsService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Контроллер событий администратора
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/events")
public class AdminEventsController {

    private final EventsService eventsService;

    /**
     * Поиск событий по фильтрам
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> getEvents(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<State> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(defaultValue = Constants.DEFAULTSTARTTIME)
            @DateTimeFormat(pattern = Constants.DATETIMEFORMAT) LocalDateTime rangeStart,
            @RequestParam(defaultValue = Constants.DEFAULTENDTIME)
            @DateTimeFormat(pattern = Constants.DATETIMEFORMAT) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        return eventsService.getEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    /**
     * Обновление события
     */
    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(@PathVariable @Positive Long eventId,
                                    @RequestBody @Valid UpdateEventAdminRequest updateEvent) {
        return eventsService.updateEventAdmin(eventId, updateEvent);
    }
}