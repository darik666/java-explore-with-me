package ru.practicum.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.EndpointHitDto;
import ru.practicum.StatsClient;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class EwmClient extends StatsClient {
    @Value("emw-service")
    private String appName;

    @Autowired
    public EwmClient(@Value("${ewm-stats.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .build()
        );
    }

    public Object getViews(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        String url = "/stats?start=" + start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                + "&end=" + end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                + "&uris=" + String.join(",", uris)
                + "&unique=" + unique;
        log.debug("ewm-service отправил GET запрос: {} на state-server", url);
        return getStats(url).getBody();
    }

    public void addHit(HttpServletRequest request) {
        log.debug("ewm-service отправил POST запрос: {} на stats-server", request.getRequestURI());
        postStats("/hit", new EndpointHitDto(appName, request.getRequestURI(), request.getRemoteAddr()));
    }
}
