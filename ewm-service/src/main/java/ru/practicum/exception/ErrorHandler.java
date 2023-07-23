package ru.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.dto.ApiError;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ErrorHandler {

        @ExceptionHandler
        @ResponseStatus(HttpStatus.NOT_FOUND)
        public ApiError handleNotFoundException(final NotFoundException e) {
            String status = String.valueOf(HttpStatus.NOT_FOUND);
            String reason = "The required object was not found.";
            String message = e.getMessage();
            LocalDateTime time = LocalDateTime.now();
            return new ApiError(message, reason, status, time);
        }

        @ExceptionHandler
        @ResponseStatus(HttpStatus.NOT_FOUND)
        public ApiError handleEventValidationException(final EventValidationException e) {
            String status = String.valueOf(HttpStatus.BAD_REQUEST);
            String reason = "Incorrectly made request.";
            String message = e.getMessage();
            LocalDateTime time = LocalDateTime.now();
            return new ApiError(message, reason, status, time);
        }
}