package ru.practicum.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.State;
import ru.practicum.ViewStatsDto;
import ru.practicum.client.EwmClient;
import ru.practicum.dto.*;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Event;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.ParticipationRequestRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.categories.CategoryMapper;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class EventMapper {
    private CategoryRepository categoryRepository;
    private ParticipationRequestRepository requestRepository;
    private UserRepository userRepository;
    private EwmClient client;

    public Event fromNewDtoToEvent(Long userId, NewEventDto dto) {
        Event event = new Event();
        event.setEventDate(dto.getEventDate());
        event.setState(State.PENDING);
        event.setAnnotation(dto.getAnnotation());
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setCategory(categoryRepository.findById(dto.getCategory())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Категория с id=%d не найдена", dto.getCategory()))));
        event.setInitiator(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Пользователь с id=%d не найден", userId))));
        event.setLat(dto.getLocation().getLat());
        event.setLon(dto.getLocation().getLon());
        event.setParticipantLimit(dto.getParticipantLimit());
        event.setPaid(dto.getPaid());
        event.setRequestModeration(dto.getRequestModeration());
        event.setCreated(LocalDateTime.now());
        return event;
    }

    @Transactional
    public EventFullDto toFullDto(Event event) {
        EventFullDto dto = new EventFullDto();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setConfirmedRequests(requestRepository.getConfirmedRequestsByEventId(event.getId()));
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

        List<String> uris = new ArrayList<>();
        uris.add("/events/" + event.getId());
        List<ViewStatsDto> responseList;
        try {
            responseList = client.getViews(
                    dto.getEventDate().minusYears(99),
                    dto.getEventDate().plusYears(99),
                    uris,
                    true
            );

            if (!responseList.isEmpty()) {
                ViewStatsDto viewStatsDto = responseList.get(0);
                dto.setViews(viewStatsDto.getHits().intValue());
            } else {
                dto.setViews(0);
            }
        } catch (Exception e) {
            dto.setViews(null);
        }

        return dto;
    }

    @Transactional
    public EventShortDto toShortDto(Event event) {
        EventShortDto dto = new EventShortDto();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setConfirmedRequests(requestRepository.getConfirmedRequestsByEventId(event.getId()));
        dto.setAnnotation(event.getAnnotation());
        dto.setCategory(CategoryMapper.toCategoryDto(event.getCategory()));
        dto.setInitiator(UserMapper.toUserShortDto(event.getInitiator()));
        dto.setPaid(event.getPaid());
        dto.setEventDate(event.getEventDate());

        List<String> uris = new ArrayList<>();
        uris.add("/events/" + event.getId());
        try {
            Object views = client.getViews(
                    dto.getEventDate().minusYears(99),
                    dto.getEventDate().plusYears(99),
                    uris,
                    true
            );
            List<ViewStatsDto> viewStats = (List<ViewStatsDto>) views;
            if (!viewStats.isEmpty()) {
                dto.setViews(viewStats.get(0).getHits().longValue());
            } else {
                dto.setViews(0L);
            }
        } catch (Exception e) {
            dto.setViews(null);
        }

        return dto;
    }

    public Event toEventFromAdmUpdateDto(UpdateEventAdminRequest admDto) {
        Event event = new Event();
        event.setAnnotation(admDto.getAnnotation());
        event.setDescription(admDto.getDescription());
        event.setTitle(admDto.getTitle());
        event.setCategory(categoryRepository.findById(admDto.getCategory())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Категория с id=%d не найдена", admDto.getCategory()))));
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

        if (dto.getCategory() != null) {
            event.setCategory(categoryRepository.findById(dto.getCategory())
                    .orElseThrow(() -> new NotFoundException(
                            String.format("Категория с id=%d не найдена", dto.getCategory()))));
        }

        event.setParticipantLimit(dto.getParticipantLimit());
        event.setEventDate(dto.getEventDate());
        event.setAnnotation(dto.getAnnotation());
        return event;
    }

    public List<EventShortDto> getEventShortDtoList(List<Event> events) {
        return events.stream().map(this::toShortDto).collect(Collectors.toList());
    }
}
