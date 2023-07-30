package ru.practicum.service.comments;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.CommentShortDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.model.Comment;
import ru.practicum.model.Event;
import ru.practicum.model.User;
import ru.practicum.service.events.EventMapper;
import ru.practicum.service.users.UserMapper;

import java.time.LocalDateTime;

/**
 * Маппер комментариев
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Component
public class CommentMapper {

    private EventMapper eventMapper;

    public Comment fromNewDto(NewCommentDto dto, Event event, User author) {
        Comment comment = new Comment();
        comment.setAuthor(author);
        comment.setEvent(event);
        comment.setText(dto.getText());
        comment.setCreated(LocalDateTime.now());
        return comment;
    }

    public CommentDto toCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setAuthor(comment.getAuthor());
        commentDto.setEvent(comment.getEvent());
        commentDto.setText(comment.getText());
        commentDto.setCreated(comment.getCreated());
        return commentDto;
    }

    public CommentShortDto toShortDto(Comment comment) {
        CommentShortDto commentShortDto = new CommentShortDto();
        commentShortDto.setId(comment.getId());
        commentShortDto.setAuthor(UserMapper.toUserShortDto(comment.getAuthor()));
        commentShortDto.setText(comment.getText());
        commentShortDto.setCreated(LocalDateTime.now());
        return commentShortDto;
    }
}