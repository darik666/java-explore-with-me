package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.EndpointHitDto;
import ru.practicum.exception.StatValidationException;
import ru.practicum.StatsRepository;
import ru.practicum.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Сервисный класс сервиса статистики
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    /**
     * Сохранение просмотра эндпоинта
     */
    @Override
    @Transactional
    public ResponseEntity<Void> save(EndpointHitDto endpointHitDto) {
        log.debug("Сохранение просмотра эндпоинта: " + endpointHitDto);
        statsRepository.save(StatMapper.toStat(endpointHitDto));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Получение статистики
     */
    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (start.isAfter(end)) {
            throw new StatValidationException("Диапазон времени указан неверно");
        }
        if (uris == null || uris.isEmpty()) {
            log.debug("Получение всей статистики");
            return statsRepository.findViewStatsWithoutUris(start, end, unique);
        } else {
            log.debug("Получение статистики по списку uri");
            return statsRepository.findViewStatsWithUris(start, end, uris, unique);
        }
    }
}