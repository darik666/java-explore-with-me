package ru.practicum.service.requests;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.model.enums.EventStatus;
import ru.practicum.model.enums.State;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.exception.AlreadyExistsException;
import ru.practicum.exception.EventValidationException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Event;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.model.User;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.ParticipationRequestRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервисный класс запроса на участие в событии
 */
@Slf4j
@Service
@AllArgsConstructor
public class ParticipationRequestServiceImpl implements ParticipationRequestService {

    private ParticipationRequestRepository participationRequestRepository;
    private UserRepository userRepository;
    private EventRepository eventRepository;

    /**
     * Получение информации о заявках текущего пользователя на участие в чужих событиях
     */
    @Override
    public List<ParticipationRequestDto> getParticipationRequestsByUserId(Long requestorId) {
        log.debug("Получение заявки на участие в событии пользователя id=", requestorId);
        return participationRequestRepository.findAllByRequestorId(requestorId)
                .stream()
                .map(ParticipationRequestMapper::toParticipationDto)
                .collect(Collectors.toList());
    }

    /**
     * Добавление запроса от текущего пользователя на участие в событии
     */
    @Transactional
    @Override
    public ParticipationRequestDto createParticipationRequest(Long userId, Long eventId) {
        ParticipationRequest existingRequest =
                participationRequestRepository.findByEventIdAndRequestorId(eventId, userId);
        if (existingRequest != null) {
            log.warn("Нельзя добавить повторный запрос.");
            throw new AlreadyExistsException("Нельзя добавить повторный запрос.");
        }

        ParticipationRequest request = new ParticipationRequest();
        request.setStatus(EventStatus.PENDING);

        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%d не найден", userId)));
        request.setRequestor(user);

        Event event =  eventRepository
                .findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с id=%d не найдено", eventId)));
        request.setEvent(event);

        if (event.getInitiator().getId().equals(userId)) {
            log.warn("Инициатор события не может добавить запрос на участие в своём событии.");
            throw new IllegalStateException("Инициатор события не может добавить запрос на участие в своём событии.");
        }

        if (event.getState() != State.PUBLISHED) {
            log.warn("Нельзя участвовать в неопубликованном событии.");
            throw new EventValidationException("Нельзя участвовать в неопубликованном событии.");
        }

        if (event.getParticipantLimit() > 0
                && event.getConfirmedRequests().equals(event.getParticipantLimit())) {
            log.warn("У события достигнут лимит запросов на участие.");
            throw new EventValidationException("У события достигнут лимит запросов на участие.");
        }

        if (event.getRequestModeration() == false || event.getParticipantLimit() == 0) {
            request.setStatus(EventStatus.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        }
        request.setCreated(LocalDateTime.now());
        log.debug("Создание заявки на участие в событии: ", request);
        return ParticipationRequestMapper.toParticipationDto(participationRequestRepository.save(request));
    }

    /**
     * Отмена своего запроса на участие в событии
     */
    @Transactional
    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        ParticipationRequest participationRequest = participationRequestRepository
                .findByIdAndRequestorId(requestId, userId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие не найдено")));
        participationRequest.setStatus(EventStatus.CANCELED);
        log.debug("Отмена заявки на участие в событии: ", participationRequest);
        return ParticipationRequestMapper.toParticipationDto(participationRequestRepository.save(participationRequest));
    }
}