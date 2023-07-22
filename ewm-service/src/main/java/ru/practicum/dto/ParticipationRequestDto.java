package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.EventStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ParticipationRequestDto {
    private Long id;
    private LocalDateTime created;
    private Long event;
    private Long requester;
    private EventStatus status;
}
