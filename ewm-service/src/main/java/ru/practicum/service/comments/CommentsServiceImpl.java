package ru.practicum.service.comments;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.EwmClient;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.CommentShortDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Comment;
import ru.practicum.model.Event;
import ru.practicum.model.User;
import ru.practicum.repository.CommentsRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.events.EventMapper;
import ru.practicum.service.users.UserMapper;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервисный класс комментариев
 */
@Slf4j
@Service
@AllArgsConstructor
public class CommentsServiceImpl implements  CommentsService {

    private final CommentsRepository commentsRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentMapper commentMapper;
    private final EventMapper eventMapper;
    private final EwmClient ewmClient;

    /**
     * Создание комментария
     */
    @Override
    @Transactional
    public CommentShortDto createComment(Long userId, NewCommentDto dto) {
        log.debug("Создание комментария: ", dto);
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь с id=%d не найден.", userId)));
        Event event = eventRepository.findById(dto.getEventId()).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь с id=%d не найден.", dto.getEventId())));
        Comment comment = commentsRepository.save(commentMapper.fromNewDto(dto, event, user));
        CommentShortDto shortDto = commentMapper.toShortDto(comment);
        shortDto.setAuthor(UserMapper.toUserShortDto(user));
        shortDto.setEvent(eventMapper.toShortDto(comment.getEvent()));
        return shortDto;
    }

    /**
     * Обновление комментария(пользователь)
     */
    @Override
    @Transactional
    public CommentDto updateUserComment(Long userId, Long commentId, NewCommentDto newCommentDto) {
        log.debug("Обновление комментария с id=" + commentId + " на новый: ", newCommentDto);
        Comment comment = commentsRepository.findByIdAndEventIdAndAuthorId(
                commentId, newCommentDto.getEventId(), userId).orElseThrow(() -> new NotFoundException(
                        "Комментарий с id=" + commentId + "от пользователя id=" + userId +
                                " событию id=" + newCommentDto.getEventId() + " не найден."));
        comment.setText(newCommentDto.getText());
        return commentMapper.toCommentDto(commentsRepository.save(comment));
    }

    /**
     * Получение комментария по id
     */
    @Override
    public CommentDto getById(Long commentId, Long userId, HttpServletRequest httpServletRequest) {
        Comment comment = commentsRepository.findByIdAndAuthorId(commentId, userId).orElseThrow(
                () -> new NotFoundException("Комментарий с id=" + commentId + "от пользователя id=" +
                        userId + " событию id=" + " не найден."));
        log.debug("Получение комментария: ", comment);
        ewmClient.addHit(httpServletRequest);
        return commentMapper.toCommentDto(comment);
    }

    /**
     * Получение комментариев пользователя
     */
    @Override
    public List<CommentDto> getAll(Long userId,
                                   LocalDateTime rangeStart,
                                   LocalDateTime rangeEnd,
                                   int from,
                                   int size) {
        Pageable pageable = PageRequest.of(from, size);
        List<Comment> comments = commentsRepository.findAllForUser(userId, rangeStart, rangeEnd, pageable);
        log.debug("Получение списка комментариев: ", comments);
        return comments.stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    /**
     * Удаление комментария
     */
    @Override
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        Comment comment = commentsRepository.findById(commentId).orElseThrow(
                () -> new NotFoundException("Комментарий с id=" + commentId + " не найден."));
        log.debug("Удаление комментария: ", comment);
        commentsRepository.delete(comment);
    }


    /**
     * Обновление комментария(админмистратор)
     */
    @Override
    @Transactional
    public CommentDto updateAdminComment(Long commentId, NewCommentDto newCommentDto) {
        Comment comment = commentsRepository.findByIdAndEventId(
                commentId, newCommentDto.getEventId()).orElseThrow(() -> new NotFoundException(
                        "Комментарий с id=" + commentId +
                                "событию id=" + newCommentDto.getEventId() + " не найден."));
        comment.setText(newCommentDto.getText());
        log.debug("Обновление комментария с id=" + commentId + " на новый: ", newCommentDto);
        return commentMapper.toCommentDto(commentsRepository.save(comment));
    }

    /**
     * Получение комментариев(администратор)
     */
    @Override
    public List<CommentDto> getAllAdmin(String text,
                                   LocalDateTime rangeStart,
                                   LocalDateTime rangeEnd,
                                   int from,
                                   int size) {
        Pageable pageable = PageRequest.of(from, size);
        List<Comment> comments = commentsRepository.findAllAdmin(text, rangeStart, rangeEnd, pageable);
        log.debug("Получение списка комментариев(администратор): ", comments);
        return comments.stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    /**
     * Получение комментариев(публичное)
     */
    @Override
    public List<CommentShortDto> getCommentsPublic(Long eventId, int from, int size, HttpServletRequest httpServletRequest) {
        Pageable pageable = PageRequest.of(from, size);
        List<Comment> commentCheck = commentsRepository.findAllByEventIdOrderByCreatedDesc(eventId, pageable);
        if (commentCheck.isEmpty()) {
            throw new NotFoundException("Комментариев к событию id=" + eventId + " не найдено.");
        }
        List<Comment> comments = commentsRepository.findAllByEventIdOrderByCreatedDesc(eventId, pageable);
        log.debug("Получение списка комментариев к событию с id=" + eventId + " : ", comments);
        ewmClient.addHit(httpServletRequest);
        return comments.stream()
                .map(comment -> {
                    CommentShortDto dto = commentMapper.toShortDto(comment);
                    dto.setAuthor(UserMapper.toUserShortDto(comment.getAuthor()));
                    dto.setEvent(eventMapper.toShortDto(comment.getEvent()));
                    return dto;
                })
                .collect(Collectors.toList());
    }
}