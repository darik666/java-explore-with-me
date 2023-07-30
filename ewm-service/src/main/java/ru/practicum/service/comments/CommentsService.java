package ru.practicum.service.comments;

import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.CommentShortDto;
import ru.practicum.dto.comment.NewCommentDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface CommentsService {

    CommentShortDto createComment(Long userId, NewCommentDto dto);

    CommentDto updateUserComment(Long userId, Long commentId, NewCommentDto newCommentDto);

    CommentDto getById(Long commentId, Long userId, HttpServletRequest httpServletRequest);

    List<CommentDto> getAll(Long userId, LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size);

    void deleteComment(Long userId, Long commentId);

    CommentDto updateAdminComment(Long commentId, NewCommentDto newCommentDto);

    List<CommentDto> getAllAdmin(String text, LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size);

    List<CommentShortDto> getCommentsPublic(Long eventId, int from, int size, HttpServletRequest httpServletRequest);
}
