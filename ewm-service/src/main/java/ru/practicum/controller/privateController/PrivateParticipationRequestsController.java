package ru.practicum.controller.privateController;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.service.requests.ParticipationRequestService;

import javax.validation.constraints.Positive;
import java.util.List;

/**
 * Приватный контроллер запросов на участие в событии
 */
@RestController
@AllArgsConstructor
@RequestMapping(path = "/users/{userId}/requests")
public class PrivateParticipationRequestsController {

    private final ParticipationRequestService participationRequestService;

    /**
     * Получение информации о заявках текущего пользователя на участие в чужих событиях
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getParticipationRequestsByUserId(
            @PathVariable @Positive Long userId) {
        return participationRequestService.getParticipationRequestsByUserId(userId);
    }

    /**
     * Добавление запроса от текущего пользователя на участие в событии
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto postPartRequest(@PathVariable @Positive Long userId,
                                                   @RequestParam @Positive Long eventId) {
        return participationRequestService.createParticipationRequest(userId, eventId);
    }

    /**
     * Отмена своего запроса на участие в событии
     */
    @PatchMapping("/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto cancelMainPartRequest(@PathVariable @Positive Long userId,
                                                         @PathVariable @Positive Long requestId) {
        return participationRequestService.cancelRequest(userId, requestId);
    }
}