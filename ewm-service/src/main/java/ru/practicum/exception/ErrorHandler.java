package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.dto.ApiError;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.time.LocalDateTime;

/**
 * Обработчик исключений
 */
@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(final NotFoundException e) {
        log.debug("Получен статус 404 Not Found {}", e.getMessage(), e);
        String status = String.valueOf(HttpStatus.NOT_FOUND);
        String reason = "The required object was not found.";
        String message = e.getMessage();
        LocalDateTime time = LocalDateTime.now();
        return new ApiError(message, reason, status, time);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleEventValidationException(final EventValidationException e) {
        log.debug("Получен статус 409 Conflict {}", e.getMessage(), e);
        String status = String.valueOf(HttpStatus.CONFLICT);
        String reason = "Incorrectly made request.";
        String message = e.getMessage();
        LocalDateTime time = LocalDateTime.now();
        return new ApiError(message, reason, status, time);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMissingServletRequestParameterException(final MissingServletRequestParameterException e) {
        log.debug("Получен статус 400 Bad Request {}", e.getMessage(), e);
        String status = String.valueOf(HttpStatus.BAD_REQUEST);
        String reason = "Required request parameter is missing";
        String message = e.getMessage();
        LocalDateTime time = LocalDateTime.now();
        return new ApiError(message, reason, status, time);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDataIntegrityViolationException(final DataIntegrityViolationException e) {
        log.debug("Получен статус 409 Conflict {}", e.getMessage(), e);
        String status = String.valueOf(HttpStatus.CONFLICT);
        String reason = "Integrity constraint has been violated.";
        String message = e.getMessage();
        LocalDateTime time = LocalDateTime.now();
        return new ApiError(message, reason, status, time);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.debug("Получен статус 400 Bad Request {}", e.getMessage(), e);
        String status = String.valueOf(HttpStatus.BAD_REQUEST);
        String reason = "Validation failed for method argument";
        String message = e.getMessage();
        LocalDateTime time = LocalDateTime.now();
        return new ApiError(message, reason, status, time);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(final ValidationException e) {
        log.debug("Получен статус 400 Bad Request {}", e.getMessage(), e);
        String status = String.valueOf(HttpStatus.BAD_REQUEST);
        String reason = "Validation failed for argument";
        String message = e.getMessage();
        LocalDateTime time = LocalDateTime.now();
        return new ApiError(message, reason, status, time);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConstraintViolationException(final ConstraintViolationException e) {
        log.debug("Получен статус 409 Conflict {}", e.getMessage(), e);
        String status = String.valueOf(HttpStatus.CONFLICT);
        String reason = "Validation failed for argument";
        String message = e.getMessage();
        LocalDateTime time = LocalDateTime.now();
        return new ApiError(message, reason, status, time);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleIllegalStateException(final IllegalStateException e) {
        log.debug("Получен статус 409 Conflict {}", e.getMessage(), e);
        String status = String.valueOf(HttpStatus.CONFLICT);
        String reason = "Illegal State Exception";
        String message = e.getMessage();
        LocalDateTime time = LocalDateTime.now();
        return new ApiError(message, reason, status, time);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleAlreadyExistsException(final AlreadyExistsException e) {
        log.debug("Получен статус 409 Conflict {}", e.getMessage(), e);
        String status = String.valueOf(HttpStatus.CONFLICT);
        String reason = "Already exists";
        String message = e.getMessage();
        LocalDateTime time = LocalDateTime.now();
        return new ApiError(message, reason, status, time);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleIllegalArgumentException(final IllegalArgumentException e) {
        log.debug("Получен статус 400 Bad Request {}", e.getMessage(), e);
        String status = String.valueOf(HttpStatus.BAD_REQUEST);
        String reason = "Validation failed for argument";
        String message = e.getMessage();
        LocalDateTime time = LocalDateTime.now();
        return new ApiError(message, reason, status, time);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleException(final Exception e) {
        e.printStackTrace();
        log.debug("Получен статус 500 Internal Server Error {}", e.getMessage(), e);
        String status = String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR);
        String reason = "INTERNAL_SERVER_ERROR";
        String message = e.getMessage();
        LocalDateTime time = LocalDateTime.now();
        return new ApiError(message, reason, status, time);
    }
}