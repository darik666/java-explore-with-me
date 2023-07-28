package ru.practicum.service.events;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.*;
import ru.practicum.client.EwmClient;
import ru.practicum.dto.event.*;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.exception.EventValidationException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.model.User;
import ru.practicum.model.enums.*;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.ParticipationRequestRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.requests.ParticipationRequestMapper;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервисный класс событий
 */
@Slf4j
@Service
@AllArgsConstructor
public class EventsServiceImpl implements EventsService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final ParticipationRequestRepository participationRequestRepository;
    private final UserRepository userRepository;
    private final EwmClient ewmClient;
    private final EventMapper eventMapper;

    /**
     * Поиск событий по фильтрам
     */
    @Override
    public List<EventFullDto> getEvents(List<Long> users,
                                        List<State> states,
                                        List<Long> categories,
                                        LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd,
                                        int from,
                                        int size) {
        Pageable pageable = PageRequest.of(from, size);
        Page<Event> eventPage = eventRepository.getEventsAdmin(
                users, states, categories, rangeStart, rangeEnd, pageable);
        List<Event> events = eventPage.getContent();
        log.debug("Получение списка событий: ", events);
        return getEventFullDtoList(events);
    }

    /**
     * Обновление события
     */
    @Transactional
    @Override
    public EventFullDto updateEventAdmin(Long eventId, UpdateEventAdminRequest updateEvent) {
        Event event = findEvent(eventId);

        LocalDateTime publishedOn = event.getPublishedOn();
        LocalDateTime updatedEventDate = updateEvent.getEventDate();
        if (publishedOn != null && updatedEventDate != null
                && updatedEventDate.isBefore(publishedOn.plusHours(1))) {
            log.warn("Дата начала изменяемого события должна быть не ранее чем за час от даты публикации.");
            throw new EventValidationException("Дата начала изменяемого события должна быть " +
                    "не ранее чем за час от даты публикации.");
        }

        if (updateEvent.getStateAction() == AdminActionEnum.PUBLISH_EVENT) {
            if (event.getState() == State.PENDING) {
                event.setState(State.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else {
                log.warn("Cобытие можно публиковать, только если оно в состоянии ожидания публикации.");
                throw new EventValidationException("Cобытие можно публиковать, " +
                        "только если оно в состоянии ожидания публикации.");
            }
        }

        if (updateEvent.getStateAction() == AdminActionEnum.REJECT_EVENT) {
            if (event.getState() != State.PUBLISHED) {
                event.setState(State.CANCELED);
            } else {
                log.warn("Cобытие можно отклонить, только если оно еще не опубликовано.");
                throw new EventValidationException("Cобытие можно отклонить, " +
                        "только если оно еще не опубликовано.");
            }
        }
        Event result = eventRepository.save(updateAdmEvent(event, updateEvent));
        EventFullDto resultDto = eventMapper.toFullDto(result);
        resultDto.setViews(getViews(resultDto));
        resultDto.setConfirmedRequests(
                participationRequestRepository.getConfirmedRequestsByEventId(event.getId()));
        log.debug("Обновление администратором события: ", resultDto);
        return resultDto;
    }

    public Event updateAdmEvent(Event event, UpdateEventAdminRequest updateEvent) {
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
            event.setCategory(categoryRepository.findById(updateEvent.getCategory()).get());
        }

        if (updateEvent.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEvent.getParticipantLimit());
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

    /**
     * Получение событий с возможностью фильтрации
     */
    @Override
    public List<EventShortDto> getAll(String text,
                                      List<Long> categories,
                                      Boolean paid,
                                      LocalDateTime rangeStart,
                                      LocalDateTime rangeEnd,
                                      Boolean onlyAvailable,
                                      Sort sort,
                                      int from,
                                      int size,
                                      HttpServletRequest httpServletRequest) {
        List<Event> events;
        if (onlyAvailable != null && onlyAvailable) {
            events = eventRepository.findAllAvailableEvents(
                    text, categories, paid, rangeStart, rangeEnd, PageRequest.of(from, size));
        } else {
            events = eventRepository.findAllEvents(
                    text, categories, paid, rangeStart, rangeEnd, PageRequest.of(from, size));
        }

        List<String> uris = events.stream()
                .map(event -> "/events/" + event.getId())
                .collect(Collectors.toList());

        Object views = ewmClient.getViews(rangeStart, rangeEnd, uris, true);
        List<ViewStatsDto> viewStats = (List<ViewStatsDto>) views;

        List<EventShortDto> eventDtos = events.stream()
                .map(event -> {
                    Long viewsCount = viewStats.stream()
                            .filter(viewStat -> viewStat.getUri().equals("/events/" + event.getId()))
                            .mapToLong(ViewStatsDto::getHits)
                            .findFirst()
                            .orElse(0L);
                    return getEventShortDtoList(List.of(event)).get(0);
                })
                .collect(Collectors.toList());

        if (sort != null) {
            if (sort.equals(Sort.valueOf("EVENT_DATE"))) {
                eventDtos.sort(Comparator.comparing(EventShortDto::getEventDate)
                        .thenComparing(EventShortDto::getViews).reversed());
            } else if (sort.equals(Sort.valueOf("VIEWS"))) {
                eventDtos.sort(Comparator.comparing(EventShortDto::getViews)
                        .thenComparing(EventShortDto::getEventDate).reversed());
            }
        }
        ewmClient.addHit(httpServletRequest);
        log.debug("Получение списка событий: ", eventDtos);
        return eventDtos;
    }

    /**
     * Получение подробной информации об опубликованном событии по его идентификатору
     */
    @Transactional
    @Override
    public EventFullDto getById(Long id, HttpServletRequest httpServletRequest) {
        Event event = findEvent(id);
        String state = String.valueOf(event.getState());
        if (!state.equals("PUBLISHED")) {
            log.warn("Событие не опубликовано: ", event);
            throw new NotFoundException("Событие не опубликовано");
        }
        ewmClient.addHit(httpServletRequest);
        EventFullDto resultDto = eventMapper.toFullDto(event);
        resultDto.setViews(getViews(resultDto));
        resultDto.setConfirmedRequests(
                participationRequestRepository.getConfirmedRequestsByEventId(event.getId()));
        log.debug("Получение события: ", resultDto);
        return resultDto;
    }

    /**
     * Получение событий, добавленных текущим пользователем
     */
    @Override
    public List<EventShortDto> getAllByUserId(Long userId, int from, int size) {
        Pageable pageable = PageRequest.of(from, size);
        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageable);
        log.debug("Получение событий пользователя id=" + userId, events);
        return getEventShortDtoList(events);
    }

    /**
     * Добавление нового события
     */
    @Transactional
    @Override
    public EventFullDto create(Long userId, NewEventDto newEvent) {
        if (newEvent.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            log.warn("Дата и время на которые намечено событие " +
                    "не может быть раньше, чем через два часа от текущего момента");
            throw new EventValidationException("Дата и время на которые намечено событие " +
                    "не может быть раньше, чем через два часа от текущего момента");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Пользователь с id=%d не найден", userId)));
        Event event = eventMapper.fromNewDtoToEvent(newEvent);
        event.setInitiator(user);
        Category category = categoryRepository.findById(newEvent.getCategory())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Категория с id=%d не найдена", newEvent.getCategory())));
        event.setCategory(category);
        EventFullDto resultDto = eventMapper.toFullDto(eventRepository.save(event));
        resultDto.setViews(getViews(resultDto));
        resultDto.setConfirmedRequests(
                participationRequestRepository.getConfirmedRequestsByEventId(event.getId()));
        log.debug("Создание события: ", resultDto);
        return resultDto;
    }

    /**
     * Получение полной информации о событии добавленном текущим пользователем
     */
    @Override
    public EventFullDto getEventByIdAndUserId(Long userId, Long eventId) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId);
        if (event == null) {
            log.debug("Пользователь с id=%d не имеет события с id=%d", userId, eventId);
            throw new NotFoundException(String.format(
                    "Пользователь с id=%d не имеет события с id=%d", userId, eventId));
        }
        EventFullDto resultDto = eventMapper.toFullDto(event);
        resultDto.setViews(getViews(resultDto));
        resultDto.setConfirmedRequests(
                participationRequestRepository.getConfirmedRequestsByEventId(event.getId()));
        log.debug("Получение события по id и пользователю: ", resultDto);
        return resultDto;
    }

    /**
     * Изменение события добавленного текущим пользователем
     */
    @Transactional
    @Override
    public EventFullDto modifyEvent(Long userId, Long eventId, UpdateEventUserRequest updateEvent) {
        Event event = findEvent(eventId);

        Event newEvent = updateUserEvent(event, eventMapper.toEventFromUpdateDto(updateEvent));
        if (newEvent.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            log.warn("Дата и время на которые намечено событие " +
                    "не может быть раньше, чем через два часа от текущего момента");
            throw new EventValidationException("Дата и время на которые намечено событие " +
                    "не может быть раньше, чем через два часа от текущего момента");
        }
        String state = String.valueOf(event.getState());
        if (!state.equals(String.valueOf(State.CANCELED)) && !state.equals(String.valueOf(State.PENDING))) {
            log.warn("Изменить можно только отмененные события или события в состоянии ожидания модерации");
            throw new EventValidationException(String.format("Изменить можно только отмененные события " +
                    "или события в состоянии ожидания модерации"));
        }
        if (updateEvent.getStateAction() != null) {
            if (updateEvent.getStateAction() == UserActionEnum.SEND_TO_REVIEW) {
                event.setState(State.PENDING);
            } else {
                event.setState(State.CANCELED);
            }
        }
        Category category = null;
        if (updateEvent.getCategory() != null) {
            category = categoryRepository.findById(updateEvent.getCategory())
                    .orElseThrow(() -> new NotFoundException(
                            String.format("Категория с id=%d не найдена", updateEvent.getCategory())));
            newEvent.setCategory(category);
        }
        EventFullDto resultDto = eventMapper.toFullDto(eventRepository.save(newEvent));
        resultDto.setViews(getViews(resultDto));
        resultDto.setConfirmedRequests(
                participationRequestRepository.getConfirmedRequestsByEventId(event.getId()));
        log.debug("Обновление события: ", resultDto);
        return resultDto;
    }

    public Event updateUserEvent(Event event, Event updateEvent) {
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
            event.setCategory(categoryRepository.findById(updateEvent.getCategory().getId()).get());
        }

        if (updateEvent.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEvent.getParticipantLimit());
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

    /**
     * Получение информации о запросах на участие в событии текущего пользователя
     */
    @Override
    public List<ParticipationRequestDto> getParticipationRequests(Long userId, Long eventId) {
        userRepository.findById(userId);
        findEvent(eventId);
        log.debug("Получение запросов на участие пользовтеля id=%d в событии id=%d ", userId, eventId);
        return participationRequestRepository.findAllByEventId(eventId)
                .stream()
                .map(ParticipationRequestMapper::toParticipationDto)
                .collect(Collectors.toList());
    }

    /**
     * Изменение статуса (подтверждена, отменена) заявок на участие в событии текущего пользователя
     */
    @Transactional
    @Override
    public EventRequestStatusUpdateResult setStatusParticipationRequest(
            Long userId,
            Long eventId,
            EventRequestStatusUpdateRequest eventRequestStatus) {
        Event event = findEvent(eventId);
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%d не найден", userId)));

        if (event.getConfirmedRequests().equals(event.getParticipantLimit())) {
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
                participationRequestRepository.findAllByEventIdAndIdIn(
                        eventId, eventRequestStatus.getRequestIds());


        if (event.getRequestModeration() && event.getParticipantLimit() >= 0) {
            for (ParticipationRequest request : requests) {
                if (request.getStatus() == EventStatus.PENDING
                        && eventRequestStatus.getStatus().equals(EventStatus.CONFIRMED)) {
                    request.setStatus(EventStatus.CONFIRMED);
                    event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                    eventRepository.save(event);
                } else if (request.getStatus() == EventStatus.PENDING
                        && eventRequestStatus.getStatus().equals(EventStatus.REJECTED)) {
                    request.setStatus(EventStatus.REJECTED);
                    event.setConfirmedRequests(event.getConfirmedRequests() - 1);
                    eventRepository.save(event);
                } else {
                    throw new EventValidationException(
                            "Cтатус можно изменить только у заявок, находящихся в состоянии ожидания");
                }
            }
        }
        eventRepository.save(event);
        List<ParticipationRequest> resultRequests = participationRequestRepository.saveAll(requests);
        EventRequestStatusUpdateResult resultResponse = new EventRequestStatusUpdateResult();
        for (ParticipationRequest request: resultRequests) {
            if (request.getStatus() == EventStatus.CONFIRMED) {
                resultResponse.getConfirmedRequests().add(ParticipationRequestMapper.toParticipationDto(request));
            } else if (request.getStatus() == EventStatus.REJECTED) {
                resultResponse.getRejectedRequests().add(ParticipationRequestMapper.toParticipationDto(request));
            }
        }
        log.debug("Изменение статуса запросов на участие в событии: ", resultResponse);
        return resultResponse;
    }

    public Event findEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с id=%d не найдено", eventId)));
    }

    @Override
    public Long getViews(EventShortDto event) {
        Long views;
        List<String> uris = new ArrayList<>();
        uris.add("/events/" + event.getId());
        List<ViewStatsDto> responseList;
        try {
            responseList = ewmClient.getViews(
                    event.getEventDate().minusYears(99),
                    event.getEventDate().plusYears(99),
                    uris,
                    true
            );

            if (!responseList.isEmpty()) {
                ViewStatsDto viewStatsDto = responseList.get(0);
                views = viewStatsDto.getHits().longValue();
            } else {
                views = 0L;
            }
        } catch (Exception e) {
            views = null;
        }
        log.debug("Получение количества просмотров события:", event);
        return views;
    }

    @Override
    public List<EventShortDto> getEventShortDtoList(List<Event> events) {
        List<EventShortDto> eventShortDtos = events.stream()
                .map(eventMapper::toShortDto)
                .collect(Collectors.toList());
        eventShortDtos.forEach(event -> event.setViews(getViews(event)));
        eventShortDtos.forEach(event -> event.setConfirmedRequests(
                participationRequestRepository.getConfirmedRequestsByEventId(event.getId())));
        return eventShortDtos;
    }

    @Override
    public List<EventFullDto> getEventFullDtoList(List<Event> events) {
        List<EventFullDto> eventFullDtos = events.stream()
                .map(eventMapper::toFullDto)
                .collect(Collectors.toList());
        eventFullDtos.forEach(event -> event.setViews(getViews(event)));
        eventFullDtos.forEach(event -> event.setConfirmedRequests(
                participationRequestRepository.getConfirmedRequestsByEventId(event.getId())));
        return eventFullDtos;
    }
}