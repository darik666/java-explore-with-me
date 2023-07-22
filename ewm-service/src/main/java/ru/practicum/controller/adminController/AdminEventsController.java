package ru.practicum.controller.adminController;

import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.EventStatus;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.UpdateEventAdminRequest;
import ru.practicum.service.events.EventsService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/admin/events")
public class AdminEventsController {
    private final EventsService eventsService;

    @GetMapping
    public List<EventFullDto> getEvents(
            @RequestParam(value = "users", required = false) List<Long> users,
            @RequestParam(value = "states", required = false) List<EventStatus> states,
            @RequestParam(value = "categories", required = false) List<Long> categories,
            @RequestParam(value = "rangeStart", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(value = "rangeEnd", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        return eventsService.getEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable @Positive Long eventId,
                                       @RequestBody UpdateEventAdminRequest updateEvent) {
        return eventsService.updateEventAdmin(eventId, updateEvent);
    }
}
