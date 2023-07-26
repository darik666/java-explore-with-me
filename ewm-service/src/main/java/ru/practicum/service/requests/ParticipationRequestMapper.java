package ru.practicum.service.requests;

import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.model.ParticipationRequest;

/**
 * Маппер запросов на участие в событии
 */
public class ParticipationRequestMapper {
    public static ParticipationRequestDto toParticipationDto(ParticipationRequest part) {
        ParticipationRequestDto dto = new ParticipationRequestDto();
        dto.setId(part.getId());
        dto.setEvent(part.getEvent().getId());
        dto.setRequester(part.getRequestor().getId());
        dto.setStatus(part.getStatus());
        dto.setCreated(part.getCreated());
        return dto;
    }
}