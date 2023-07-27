package ru.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.enums.State;
import ru.practicum.model.Event;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Репозиторий событий
 */
public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e FROM Event e " +
            "WHERE (e.initiator.id IN (:users) OR :users IS NULL) " +
            "AND (e.state IN (:states) OR :states IS NULL) " +
            "AND (e.category.id IN (:categories) OR :categories IS NULL) " +
            "AND (e.eventDate BETWEEN :rangeStart AND :rangeEnd) " +
            "ORDER BY e.created DESC")
    Page<Event> getEventsAdmin(@Param("users") List<Long> users,
                               @Param("states") List<State> states,
                               @Param("categories") List<Long> categories,
                               @Param("rangeStart") LocalDateTime rangeStart,
                               @Param("rangeEnd") LocalDateTime rangeEnd,
                               Pageable pageable);

    @Query("SELECT e " +
            "FROM Event e " +
            "WHERE ((LOWER(e.annotation) LIKE CONCAT('%',lower(:text),'%') " +
            "OR LOWER(e.description) LIKE CONCAT('%',lower(:text),'%')) " +
            "OR :text IS NULL ) " +
            "AND (e.category.id IN (:categories) OR :categories IS NULL) " +
            "AND (e.paid IN (:paid) OR :paid IS NULL) " +
            "AND (e.eventDate BETWEEN :rangeStart AND :rangeEnd) " +
            "AND (e.confirmedRequests < e.participantLimit)")
    List<Event> findAllAvailableEvents(@Param("text") String text,
                              @Param("categories") List<Long> categories,
                              @Param("paid") Boolean paid,
                              @Param("rangeStart") LocalDateTime rangeStart,
                              @Param("rangeEnd") LocalDateTime rangeEnd,
                              Pageable pageable);

    @Query("SELECT e " +
            "FROM Event e " +
            "WHERE ((LOWER(e.annotation) LIKE CONCAT('%',lower(:text),'%') " +
            "OR LOWER(e.description) LIKE CONCAT('%',lower(:text),'%')) " +
            "OR :text IS NULL ) " +
            "AND (e.category.id IN (:categories) OR :categories IS NULL) " +
            "AND (e.paid IN (:paid) OR :paid IS NULL) " +
            "AND (e.eventDate BETWEEN :rangeStart AND :rangeEnd)")
    List<Event> findAllEvents(@Param("text") String text,
                              @Param("categories") List<Long> categories,
                              @Param("paid") Boolean paid,
                              @Param("rangeStart") LocalDateTime rangeStart,
                              @Param("rangeEnd") LocalDateTime rangeEnd,
                              Pageable pageable);

    List<Event> findAllByInitiatorId(Long initiatorId, Pageable pageable);

    Event findByIdAndInitiatorId(Long userId, Long eventId);

    List<Event> findByCategoryId(Long categoryId);

    @Query("SELECT e.id " +
            "FROM Compilation c " +
            "JOIN c.events e " +
            "WHERE c.id = :compilationId")
    List<Long> findEventIdsByCompilationId(@Param("compilationId") Long compilationId);

    @Query("SELECT e FROM Event e " +
            "WHERE e.id IN :ids")
    List<Event> getEventsByIdList(@Param("ids") List<Long> ids);
}