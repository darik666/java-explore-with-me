package ru.practicum.controller.privateController;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.service.requests.ParticipationRequestService;

import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/users/{userId}/requests")
public class PrivateParticipationRequestsController {
    private final ParticipationRequestService participationRequestService;

    @GetMapping
    public List<ParticipationRequestDto> getParticipationRequestsByUserId(@PathVariable @Positive Long userId) {
        return participationRequestService.getParticipationRequestsByUserId(userId);
    }

    @PostMapping
    public ParticipationRequestDto postPartRequest(@PathVariable @Positive Long userId,
                                                   @RequestParam @Positive Long eventId) {
        return participationRequestService.createParticipationRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelMainPartRequest(@PathVariable @Positive Long userId,
                                                         @PathVariable @Positive Long requestId) {
        return participationRequestService.cancelRequest(userId, requestId);
    }

}
