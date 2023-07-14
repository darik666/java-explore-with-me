package ru.practicum;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

public class StatsClient {
    protected final RestTemplate rest;

    public StatsClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected ResponseEntity<Object> getStats(String path) {
        return makeAndSendRequest(HttpMethod.GET, path, null);
    }

    protected <T> ResponseEntity<Object> postStats(String path, T body) {
        return makeAndSendRequest(HttpMethod.POST, path, body);
    }

    private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method, String path, @Nullable T body) {
        HttpEntity<T> requestEntity = null;
        if (body != null) {
            requestEntity = new HttpEntity<>(body);
        }
        ResponseEntity<Object> response;
        try {
            response = rest.exchange(path, method, requestEntity, Object.class);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return prepareStatsResponse(response);
    }

    private static ResponseEntity<Object> prepareStatsResponse(ResponseEntity<Object> response) {
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