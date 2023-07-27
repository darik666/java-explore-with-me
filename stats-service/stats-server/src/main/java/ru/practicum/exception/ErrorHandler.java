package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleStatValidationException(final StatValidationException e) {
        log.debug("Получен статус 400 Bad Request {}", e.getMessage(), e);
        String status = String.valueOf(HttpStatus.BAD_REQUEST);
        String reason = "Illegal method argument";
        String message = e.getMessage();
        LocalDateTime time = LocalDateTime.now();
        return new ApiError(message, reason, status, time);
    }
}
