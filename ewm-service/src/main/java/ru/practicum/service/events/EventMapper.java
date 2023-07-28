package ru.practicum.service.events;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.dto.event.*;
import ru.practicum.model.enums.State;
import ru.practicum.model.Event;
import ru.practicum.service.users.UserMapper;
import ru.practicum.service.categories.CategoryMapper;

import java.time.LocalDateTime;

/**
 * Маппер событий
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Component
public class EventMapper {

    public Event fromNewDtoToEvent(NewEventDto dto) {
        Event event = new Event();
        event.setEventDate(dto.getEventDate());
        event.setState(State.PENDING);
        event.setAnnotation(dto.getAnnotation());
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setLat(dto.getLocation().getLat());
        event.setLon(dto.getLocation().getLon());
        event.setParticipantLimit(dto.getParticipantLimit());
        event.setPaid(dto.getPaid());
        event.setRequestModeration(dto.getRequestModeration());
        event.setCreated(LocalDateTime.now());
        return event;
    }

    public EventFullDto toFullDto(Event event) {
        EventFullDto dto = new EventFullDto();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setAnnotation(event.getAnnotation());
        dto.setDescription(event.getDescription());
        dto.setCategory(CategoryMapper.toCategoryDto(event.getCategory()));
        dto.setInitiator(UserMapper.toUserShortDto(event.getInitiator()));
        dto.setLocation(new Location(event.getLat(), event.getLon()));
        dto.setParticipantLimit(event.getParticipantLimit());
        dto.setPaid(event.getPaid());
        dto.setRequestModeration(event.getRequestModeration());
        dto.setState(String.valueOf(event.getState()));
        dto.setEventDate(event.getEventDate());
        dto.setPublishedOn(String.valueOf(event.getPublishedOn()));
        dto.setCreatedOn(String.valueOf(event.getCreated()));
        dto.setComments(event.getComments());
        return dto;
    }

    public EventShortDto toShortDto(Event event) {
        EventShortDto dto = new EventShortDto();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setAnnotation(event.getAnnotation());
        dto.setCategory(CategoryMapper.toCategoryDto(event.getCategory()));
        dto.setInitiator(UserMapper.toUserShortDto(event.getInitiator()));
        dto.setPaid(event.getPaid());
        dto.setEventDate(event.getEventDate());
        return dto;
    }

    public Event toEventFromAdmUpdateDto(UpdateEventAdminRequest admDto) {
        Event event = new Event();
        event.setAnnotation(admDto.getAnnotation());
        event.setDescription(admDto.getDescription());
        event.setTitle(admDto.getTitle());
        event.setParticipantLimit(admDto.getParticipantLimit());
        event.setEventDate(admDto.getEventDate());

        if (admDto.getLocation() != null) {
            event.setLat(admDto.getLocation().getLat());
            event.setLon(admDto.getLocation().getLon());
        }
        event.setPaid(admDto.getPaid());
        event.setRequestModeration(admDto.getRequestModeration());
        return event;
    }

    public Event toEventFromUpdateDto(UpdateEventUserRequest dto) {
        Event event = new Event();
        event.setDescription(dto.getDescription());
        event.setPaid(dto.getPaid());
        event.setTitle(dto.getTitle());
        event.setParticipantLimit(dto.getParticipantLimit());
        event.setEventDate(dto.getEventDate());
        event.setAnnotation(dto.getAnnotation());
        return event;
    }
}