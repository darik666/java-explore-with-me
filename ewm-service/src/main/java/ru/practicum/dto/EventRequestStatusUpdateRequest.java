package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.EventStatus;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventRequestStatusUpdateRequest {
    private List<Integer> requestIds;
    private EventStatus status;
}
