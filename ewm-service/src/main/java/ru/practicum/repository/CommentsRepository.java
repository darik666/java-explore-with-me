package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.Comment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий комментариев
 */
public interface CommentsRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findByIdAndEventIdAndAuthorId(Long id, Long eventId, Long authorId);

    Optional<Comment> findByIdAndAuthorId(Long id, Long authorId);

    @Query("SELECT c " +
            "FROM Comment c " +
            "WHERE c.author.id = :userId " +
            "AND c.created BETWEEN :rangeStart AND :rangeEnd " +
            "ORDER BY c.created DESC")
    List<Comment> findAllForUser(Long userId,
                                 LocalDateTime rangeStart,
                                 LocalDateTime rangeEnd,
                                 Pageable pageable);

    Optional<Comment> findByIdAndEventId(Long id, Long eventId);

    @Query("SELECT c " +
            "FROM Comment c " +
            "WHERE (LOWER(c.text) LIKE CONCAT('%',lower(:text),'%')) OR :text IS NULL " +
            "AND c.created BETWEEN :rangeStart AND :rangeEnd " +
            "ORDER BY c.created DESC")
    List<Comment> findAllAdmin(String text,
                               LocalDateTime rangeStart,
                               LocalDateTime rangeEnd,
                               Pageable pageable);

    List<Comment> findAllByEventIdOrderByCreatedDesc(Long eventId, Pageable pageable);
}