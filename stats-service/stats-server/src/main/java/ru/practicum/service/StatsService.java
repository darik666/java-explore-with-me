package ru.practicum.service;

import org.springframework.http.ResponseEntity;
import ru.practicum.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {

    ResponseEntity<Void> save(EndpointHitDto dto);

    List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}