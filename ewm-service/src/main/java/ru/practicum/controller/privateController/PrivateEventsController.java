package ru.practicum.controller.privateController;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventUserRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.service.events.EventsService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * Приватный контроллер событий
 */
@Valid
@RestController
@AllArgsConstructor
@RequestMapping(path = "/users/{userId}/events")
public class PrivateEventsController {

    private final EventsService eventService;

    /**
     * Получение событий, добавленных текущим пользователем
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getAllByUserId(
            @PathVariable @Positive Long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        return eventService.getAllByUserId(userId, from, size);
    }

    /**
     * Добавление нового события
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable @Positive Long userId,
                                    @RequestBody @Valid NewEventDto newEvent) {
        return eventService.create(userId, newEvent);
    }

    /**
     * Получение полной информации о событии добавленном текущим пользователем
     */
    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventByIdAndUserId(@PathVariable @Positive Long userId,
                                              @PathVariable @Positive Long eventId) {
        return eventService.getEventByIdAndUserId(userId, eventId);
    }

    /**
     * Изменение события добавленного текущим пользователем
     */
    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto modifyEvent(@PathVariable @Positive Long userId,
                                    @PathVariable @Positive Long eventId,
                                    @RequestBody @Valid UpdateEventUserRequest updateUser) {
        return eventService.modifyEvent(userId, eventId, updateUser);
    }

    /**
     * Получение информации о запросах на участие в событии текущего пользователя
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getParticipationRequests(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long eventId) {
        return eventService.getParticipationRequests(userId, eventId);
    }

    /**
     * Изменение статуса (подтверждена, отменена) заявок на участие в событии текущего пользователя
     */
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult setParticipationRequestStatus(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long eventId,
            @RequestBody EventRequestStatusUpdateRequest eventRequestStatus) {
        return eventService.setStatusParticipationRequest(userId, eventId, eventRequestStatus);
    }
}