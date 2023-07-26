package ru.practicum;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class StatsClient {
    protected final RestTemplate rest;

    public StatsClient(RestTemplate rest) {
        this.rest = rest;
    }

    public ResponseEntity<List<ViewStatsDto>> getStats(String path) {
        return makeAndSendRequest(HttpMethod.GET, path, null, new ParameterizedTypeReference<List<ViewStatsDto>>() {});
    }

    protected <T> ResponseEntity<T> postStats(String path, Object body) {
        return makeAndSendRequest(HttpMethod.POST, path, body, new ParameterizedTypeReference<>() {});
    }

    private <T> ResponseEntity<T> makeAndSendRequest(HttpMethod method, String path, @Nullable Object body, ParameterizedTypeReference<T> responseType) {
        HttpEntity<Object> requestEntity = null;
        if (body != null) {
            requestEntity = new HttpEntity<>(body);
        }
        ResponseEntity<T> response;
        try {
            response = rest.exchange(path, method, requestEntity, responseType);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(null);
        }
        return prepareStatsResponse(response);
    }

    private static <T> ResponseEntity<T> prepareStatsResponse(ResponseEntity<T> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }
        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());
        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }
        return responseBuilder.build();
    }
}