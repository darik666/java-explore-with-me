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

    public Object getViews(String uri) {
        log.debug("ewm-service отправил GET запрос: {} на state-server", uri);
        return getStats("/hit?uri=" + uri).getBody();
    }

    public void addHit(HttpServletRequest request) {
        log.debug("ewm-service отправил POST запрос: {} на stats-server", request.getRequestURI());
        postStats("/hit", new EndpointHitDto(appName, request.getRequestURI(), request.getRemoteAddr()));
    }
}
