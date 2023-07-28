package ru.practicum.controller.adminController;

import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.service.Constants;
import ru.practicum.service.comments.CommentsService;

import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Контроллер комментариев администратора
 */
@Valid
@RestController
@AllArgsConstructor
@RequestMapping(path = "/admin/comments")
public class AdminCommentsController {

    private final CommentsService commentsService;

    /**
     * Получение комментариев
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getAll(
            @RequestParam(defaultValue = "") String text,
            @RequestParam(defaultValue = Constants.DEFAULTSTARTTIME)
            @DateTimeFormat(pattern = Constants.DATETIMEFORMAT) LocalDateTime rangeStart,
            @RequestParam(defaultValue = Constants.DEFAULTENDTIME)
            @DateTimeFormat(pattern = Constants.DATETIMEFORMAT) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") @Positive int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        if (rangeEnd.isBefore(rangeStart)) {
            throw new ValidationException("Дата конца диапазона не может быть раньше начала.");
        }
        return commentsService.getAllAdmin(text, rangeStart, rangeEnd, from, size);
    }

    /**
     * Обновление комментария
     */
    @PatchMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto updateComment(@PathVariable Long commentId,
                                    @RequestBody NewCommentDto newCommentDto) {
        return commentsService.updateAdminComment(commentId, newCommentDto);
    }
}