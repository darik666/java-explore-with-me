package ru.practicum.service.events;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.EventStatus;
import ru.practicum.Sort;
import ru.practicum.State;
import ru.practicum.dto.*;
import ru.practicum.exception.EventValidationException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Event;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.model.User;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.ParticipationRequestRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.EventMapper;
import ru.practicum.service.ParticipationRequestMapper;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EventsServiceImpl implements EventsService {

    private final EventRepository eventRepository;
    private final ParticipationRequestRepository participationRequestRepository;
    private final UserRepository userRepository;

    @Override
    public List<EventFullDto> getEvents(List<Long> users,
                                        List<EventStatus> states,
                                        List<Long> categories,
                                        LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd,
                                        int from,
                                        int size) {
        Pageable pageable = (Pageable) PageRequest.of(from, size);
        Page<Event> eventPage = eventRepository.getEventsAdmin(users, states, categories, rangeStart, rangeEnd, pageable);
        List<Event> events = eventPage.getContent();
        return events.stream()
                .map(EventMapper::toFullDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto updateEventAdmin(Long eventId, UpdateEventAdminRequest updateEvent) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с id=%d не найдено", eventId)));

        LocalDateTime publishedOn = event.getPublishedOn();
        LocalDateTime updatedEventDate = updateEvent.getEventDate();
        if (publishedOn != null && updatedEventDate != null && updatedEventDate.isBefore(publishedOn.plusHours(1))) {
            throw new EventValidationException("Дата начала изменяемого события должна быть не ранее чем за час от даты публикации.");
        }

        if (updateEvent.getStateAction() == State.PUBLISHED && event.getState() != State.PENDING) {
            throw new EventValidationException("Cобытие можно публиковать, только если оно в состоянии ожидания публикации.");
        }

        if (updateEvent.getStateAction() == State.CANCELED && event.getState() != State.PUBLISHED) {
            throw new EventValidationException("Cобытие можно отклонить, только если оно еще не опубликовано.");
        }

        return EventMapper.toFullDto(eventRepository.save(updateEvent(event, EventMapper.toEventFromAdmUpdateDto(updateEvent))));
    }


    public Event updateEvent(Event event, Event updateEvent) {
        if (updateEvent.getTitle() != null) {
            event.setTitle(updateEvent.getTitle());
        }

        if (updateEvent.getAnnotation() != null) {
            event.setAnnotation(updateEvent.getAnnotation());
        }

        if (updateEvent.getDescription() != null) {
            event.setDescription(updateEvent.getDescription());
        }

        if (updateEvent.getCategory() != null) {
            event.setCategory(updateEvent.getCategory());
        }

        if (updateEvent.getInitiator() != null) {
            event.setInitiator(updateEvent.getInitiator());
        }

        if (updateEvent.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEvent.getParticipantLimit());
        }

        if (updateEvent.getConfirmedRequests() != null) {
            event.setConfirmedRequests(updateEvent.getConfirmedRequests());
        }

        if (updateEvent.getEventDate() != null) {
            event.setEventDate(updateEvent.getEventDate());
        }

        if (updateEvent.getPaid() != null) {
            event.setPaid(updateEvent.getPaid());
        }

        if (updateEvent.getRequestModeration() != null) {
            event.setRequestModeration(updateEvent.getRequestModeration());
        }

        return event;
    }

    @Override
    public List<EventShortDto> getAll(String text,
                                      List<Long> categories,
                                      Boolean paid, LocalDateTime rangeStart,
                                      LocalDateTime rangeEnd,
                                      Boolean onlyAvailable,
                                      Sort sort,
                                      int from,
                                      int size) {
        Pageable pageable = (Pageable) PageRequest.of(from / size, size);

        List<Event> eventPage = eventRepository.getAllEvents(text,
                categories,
                paid,
                rangeStart,
                rangeEnd,
                onlyAvailable,
                String.valueOf(sort),
                pageable).getContent();

        List<EventShortDto> eventShortDtos = eventPage
                .stream()
                .map(EventMapper::toShortDto)
                .collect(Collectors.toList());

        return eventShortDtos;
    }

    @Override
    public EventFullDto getById(Long id, HttpServletRequest httpServletRequest) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с id=%d не найдено", id)));

        if (event.getState() != State.PUBLISHED) {
            throw new EventValidationException("Событие не опубликовано");
        }

        return EventMapper.toFullDto(event);
    }

    @Override
    public List<EventShortDto> getAllByUserId(Long userId, int from, int size) {
        Pageable pageable = (Pageable) PageRequest.of(from / size, size);
        return eventRepository.findAllByInitiatorId(userId, pageable)
                .stream()
                .map(EventMapper::toShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto create(Long userId, NewEventDto newEvent) {
        if (newEvent.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new EventValidationException("Дата и время на которые намечено событие " +
                    "не может быть раньше, чем через два часа от текущего момента");
        }
        return EventMapper.toFullDto(eventRepository.save(EventMapper.fromNewDtoToEvent(userId, newEvent)));
    }

    @Override
    public EventFullDto getEventByIdAndUserId(Long userId, Long eventId) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId);
        if (event == null) {
            throw new NotFoundException(String.format(
                    "Пользователь с id=%d не имеет события с id=%d", userId, eventId));
        }
        return EventMapper.toFullDto(event);
    }

    @Override
    public EventFullDto modifyEvent(Long userId, Long eventId, UpdateEventUserRequest updateEvent) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с id=%d не найдено", eventId)));
        if (updateEvent.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new EventValidationException("Дата и время на которые намечено событие " +
                    "не может быть раньше, чем через два часа от текущего момента");
        }
        if (event.getState().equals(EventStatus.PENDING) || event.getState().equals(EventStatus.CANCELED)) {
            throw new EventValidationException(String.format("Изменить можно только отмененные события " +
                    "или события в состоянии ожидания модерации"));
        }
        eventRepository.save(updateEvent(event, EventMapper.toEventFromUpdateDto(updateEvent)));
        return EventMapper.toFullDto(event);
    }

    @Override
    public List<ParticipationRequestDto> getParticipationRequests(Long userId, Long eventId) {
        getEventByIdAndUserId(userId, eventId);

        return participationRequestRepository.findAllByEventId(eventId)
                .stream()
                .map(ParticipationRequestMapper::toParticipationDto)
                .collect(Collectors.toList());
    }


    @Override
    public EventRequestStatusUpdateResult setStatusParticipationRequest(
            Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatus) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с id=%d не найдено", eventId)));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%d не найден", userId)));

        if (event.getConfirmedRequests() == event.getParticipantLimit()) {
            List<ParticipationRequest> pendingRequests =
                    participationRequestRepository.findAllByEventIdAndStatus(event.getId(), EventStatus.PENDING);
            for (ParticipationRequest request : pendingRequests) {
                request.setStatus(EventStatus.REJECTED);
            }
            participationRequestRepository.saveAll(pendingRequests);
            throw new EventValidationException(
                    "Нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие");
        }

        List<ParticipationRequest> requests =
                participationRequestRepository.findAllByRequestor_IdAndEvent_IdAndIdIn(
                        userId, eventId, eventRequestStatus.getRequestIds());

        if (event.getRequestModeration() || event.getParticipantLimit() > 0) {
            for (ParticipationRequest request : requests) {
                if (request.getStatus() == EventStatus.PENDING) {
                    request.setStatus(eventRequestStatus.getStatus());
                } else {
                    throw new EventValidationException(
                            "Cтатус можно изменить только у заявок, находящихся в состоянии ожидания");
                }
            }
        }

        List<ParticipationRequest> resultRequests = participationRequestRepository.saveAll(requests);
        EventRequestStatusUpdateResult resultResponse = new EventRequestStatusUpdateResult();
        for (ParticipationRequest request: resultRequests) {
            if (request.getStatus() == EventStatus.CONFIRMED) {
                resultResponse.getConfirmedRequests().add(ParticipationRequestMapper.toParticipationDto(request));
            } else if (request.getStatus() == EventStatus.REJECTED) {
                resultResponse.getRejectedRequests().add(ParticipationRequestMapper.toParticipationDto(request));
            }
        }

        return resultResponse;
    }
}
