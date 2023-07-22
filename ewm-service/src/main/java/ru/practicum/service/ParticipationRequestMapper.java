package ru.practicum.service;

import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.model.ParticipationRequest;

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
