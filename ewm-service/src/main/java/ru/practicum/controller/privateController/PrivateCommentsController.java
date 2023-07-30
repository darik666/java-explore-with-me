package ru.practicum.controller.privateController;

import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.CommentShortDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.service.Constants;
import ru.practicum.service.comments.CommentsService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Приватный контроллер комментариев
 */
@RestController
@AllArgsConstructor
@RequestMapping(path = "/users/{userId}/comments")
public class PrivateCommentsController {

    private final CommentsService commentsService;

    /**
     * Создание комментария
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentShortDto createComment(@PathVariable Long userId,
                                         @RequestBody @Valid NewCommentDto newCommentDto) {
        return commentsService.createComment(userId,newCommentDto);
    }

    /**
     * Обновление комментария
     */
    @PatchMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto updateComment(@PathVariable Long userId,
                                    @PathVariable Long commentId,
                                    @RequestBody @Valid NewCommentDto newCommentDto) {
        return commentsService.updateUserComment(userId, commentId, newCommentDto);
    }

    /**
     * Получение комментария по id
     */
    @GetMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto getById(@PathVariable Long userId,
                              @PathVariable Long commentId,
                              HttpServletRequest httpServletRequest) {
        return commentsService.getById(commentId, userId, httpServletRequest);
    }

    /**
     * Получение комментариев пользователя
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getAll(
            @PathVariable Long userId,
            @RequestParam(defaultValue = Constants.DEFAULTSTARTTIME)
            @DateTimeFormat(pattern = Constants.DATETIMEFORMAT) LocalDateTime rangeStart,
            @RequestParam(defaultValue = Constants.DEFAULTENDTIME)
            @DateTimeFormat(pattern = Constants.DATETIMEFORMAT) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") @Positive int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        if (rangeEnd.isBefore(rangeStart)) {
            throw new ValidationException("Дата конца диапазона не может быть раньше начала.");
        }
        return commentsService.getAll(userId, rangeStart, rangeEnd, from, size);
    }

    /**
     * Удаление комментария
     */
    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long userId,
                              @PathVariable Long commentId) {
        commentsService.deleteComment(userId, commentId);
    }
}