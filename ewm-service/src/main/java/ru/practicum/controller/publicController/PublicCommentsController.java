package ru.practicum.controller.publicController;


import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.comment.CommentShortDto;
import ru.practicum.service.comments.CommentsService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

/**
 * Публичный контроллер комментариев
 */
@Valid
@RestController
@AllArgsConstructor
@RequestMapping(path = "/comments/{eventId}")
public class PublicCommentsController {

    private final CommentsService commentsService;

    /**
     * Получение комментариев
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentShortDto> getComments(@PathVariable Long eventId,
                                             @RequestParam(defaultValue = "0") @Positive int from,
                                             @RequestParam(defaultValue = "10") @Positive int size,
                                             HttpServletRequest httpServletRequest) {
        return commentsService.getCommentsPublic(eventId, from, size, httpServletRequest);
    }
}