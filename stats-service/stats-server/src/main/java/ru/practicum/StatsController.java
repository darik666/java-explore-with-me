package ru.practicum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.service.StatsService;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Контроллер сервиса статистики
 */
@RestController
@RequestMapping
public class StatsController {
    private final StatsService service;
    public static final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public StatsController(StatsService service) {
        this.service = service;
    }

    /**
     * Сохранение просмотра эндпоинта
     */
    @PostMapping("/hit")
    public ResponseEntity<Void> saveHit(@RequestBody EndpointHitDto dto) {
        ResponseEntity<Void> response = service.save(dto);
        return response;
    }

    /**
     * Получение статистики
     */
    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(@RequestParam String start,
                                       @RequestParam String end,
                                       @RequestParam(required = false) List<String> uris,
                                       @RequestParam(defaultValue = "false") Boolean unique) {
        return service.getStats(LocalDateTime.parse(URLDecoder.decode(start, StandardCharsets.UTF_8), format
        ), LocalDateTime.parse(URLDecoder.decode(end, StandardCharsets.UTF_8), format), uris, unique);
    }
}