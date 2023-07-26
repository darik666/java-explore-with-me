package ru.practicum.controller.privateController;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.*;
import ru.practicum.service.events.EventsService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Valid
@RestController
@AllArgsConstructor
@RequestMapping(path = "/users/{userId}/events")
public class PrivateEventsController {
    private final EventsService eventService;


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getAllByUserId(@PathVariable @Positive Long userId,
                                              @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                              @RequestParam(defaultValue = "10") @Positive int size) {
        return eventService.getAllByUserId(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable @Positive Long userId,
                                    @RequestBody @Valid NewEventDto newEvent) {
        return eventService.create(userId, newEvent);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventByIdAndUserId(@PathVariable @Positive Long userId,
                                              @PathVariable @Positive Long eventId) {
        return eventService.getEventByIdAndUserId(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto modifyEvent(@PathVariable @Positive Long userId,
                                    @PathVariable @Positive Long eventId,
                                    @RequestBody @Valid UpdateEventUserRequest updateUser) {
        return eventService.modifyEvent(userId, eventId, updateUser);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getParticipationRequests(@PathVariable @Positive Long userId,
                                                                  @PathVariable @Positive Long eventId) {
        return eventService.getParticipationRequests(userId, eventId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult setParticipationRequestStatus(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long eventId,
            @RequestBody EventRequestStatusUpdateRequest eventRequestStatus) {
        return eventService.setStatusParticipationRequest(userId, eventId, eventRequestStatus);
    }
}
