package ru.practicum.service.events;

import ru.practicum.EventStatus;
import ru.practicum.Sort;
import ru.practicum.dto.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventsService {
    List<EventFullDto> getEvents(List<Long> users,
                                          List<EventStatus> states,
                                          List<Long> categories,
                                          LocalDateTime rangeStart,
                                          LocalDateTime rangeEnd,
                                          int from,
                                          int size);

    EventFullDto updateEventAdmin(Long eventId, UpdateEventAdminRequest updateEvent);

    List<EventShortDto> getAll(String text,
                               List<Long> categories,
                               Boolean paid, LocalDateTime rangeStart,
                               LocalDateTime rangeEnd,
                               Boolean onlyAvailable,
                               Sort sort,
                               int from,
                               int size);

    EventFullDto getById(Long eventId, HttpServletRequest httpServletRequest);

    List<EventShortDto> getAllByUserId(Long userId, int from, int size);

    EventFullDto create(Long userId, NewEventDto newEvent);

    EventFullDto getEventByIdAndUserId(Long userId, Long eventId);

    EventFullDto modifyEvent(Long userId, Long eventId, UpdateEventUserRequest updateEvent);

    List<ParticipationRequestDto> getParticipationRequests(Long userId, Long eventId);

    EventRequestStatusUpdateResult setStatusParticipationRequest(
            Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatus);
}
