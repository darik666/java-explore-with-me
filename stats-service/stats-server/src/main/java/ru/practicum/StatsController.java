package ru.practicum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping
public class StatsController {
    private final StatsService service;

    @Autowired
    public StatsController(StatsService service) {
        this.service = service;
    }

    @PostMapping("/hit")
    public void saveHit(@RequestBody EndpointHitDto dto) {
        service.save(dto);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                       @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                       @RequestParam(required = false) List<String> uris,
                                       @RequestParam(defaultValue = "false") Boolean unique) {
        return service.getStats(start, end, uris, unique);
    }
}
