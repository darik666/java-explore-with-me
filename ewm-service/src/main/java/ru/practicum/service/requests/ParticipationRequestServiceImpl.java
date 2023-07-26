package ru.practicum.service.requests;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.EventStatus;
import ru.practicum.State;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.exception.AlreadyExistsException;
import ru.practicum.exception.EventValidationException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Event;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.model.User;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.ParticipationRequestRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.ParticipationRequestMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ParticipationRequestServiceImpl implements ParticipationRequestService {
    private ParticipationRequestRepository participationRequestRepository;
    private UserRepository userRepository;
    private EventRepository eventRepository;

    @Override
    public List<ParticipationRequestDto> getParticipationRequestsByUserId(Long requestorId) {
        return participationRequestRepository.findAllByRequestorId(requestorId)
                .stream()
                .map(ParticipationRequestMapper::toParticipationDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ParticipationRequestDto createParticipationRequest(Long userId, Long eventId) {
        ParticipationRequest existingRequest =
                participationRequestRepository.findByEventIdAndRequestorId(eventId, userId);
        if (existingRequest != null) {
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
            throw new IllegalStateException("Инициатор события не может добавить запрос на участие в своём событии.");
        }

        if (event.getState() != State.PUBLISHED) {
            throw new EventValidationException("Нельзя участвовать в неопубликованном событии.");
        }

        if (event.getParticipantLimit() > 0
                && event.getConfirmedRequests().equals(event.getParticipantLimit())) {
            throw new EventValidationException("У события достигнут лимит запросов на участие.");
        }

        if (event.getRequestModeration() == false || event.getParticipantLimit() == 0) {
            request.setStatus(EventStatus.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        }
        request.setCreated(LocalDateTime.now());
        return ParticipationRequestMapper.toParticipationDto(participationRequestRepository.save(request));
    }

    @Transactional
    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        ParticipationRequest participationRequest = participationRequestRepository
                .findByIdAndRequestorId(requestId, userId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие не найдено")));

        participationRequest.setStatus(EventStatus.CANCELED);
        return ParticipationRequestMapper.toParticipationDto(participationRequestRepository.save(participationRequest));
    }
}
