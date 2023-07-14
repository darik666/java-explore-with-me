package ru.practicum;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    @Override
    @Transactional
    public void save(EndpointHitDto endpointHitDto) {
        statsRepository.save(StatMapper.toStat(endpointHitDto));
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (uris == null || uris.isEmpty()) {
            return statsRepository.findViewStatsWithoutUris(start, end, unique);
        } else {
            return statsRepository.findViewStatsWithUris(start, end, uris, unique);
        }
    }
}