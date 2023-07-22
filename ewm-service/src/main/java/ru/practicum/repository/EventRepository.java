package ru.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.EventStatus;
import ru.practicum.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e FROM Event e " +
            "WHERE (:users IS NULL OR e.initiator.id IN (:users)) " +
            "AND (:states IS NULL OR e.state IN (:states)) " +
            "AND (:categories IS NULL OR e.category.id IN (:categories)) " +
            "AND (:rangeStart IS NULL OR e.eventDate >= :rangeStart) " +
            "AND (:rangeEnd IS NULL OR e.eventDate <= :rangeEnd) " +
            "AND (:publishedAfter IS NULL OR e.publishedOn IS NULL OR e.publishedOn <= :publishedAfter) " +
            "AND (:eventDateAfter IS NULL OR e.eventDate IS NULL OR e.eventDate >= :eventDateAfter) " +
            "ORDER BY e.created DESC")
    Page<Event> getEventsAdmin(@Param("users") List<Long> users,
                               @Param("states") List<EventStatus> states,
                               @Param("categories") List<Long> categories,
                               @Param("rangeStart") LocalDateTime rangeStart,
                               @Param("rangeEnd") LocalDateTime rangeEnd,
                               Pageable pageable);

    @Query("SELECT e FROM Event e " +
            "WHERE (e.publishedOn IS NOT NULL) " +
            "AND (:text IS NULL OR lower(e.annotation) LIKE %:text% OR lower(e.description) LIKE %:text%) " +
            "AND (:categories IS NULL OR e.category.id IN (:categories)) " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "AND (COALESCE(:rangeStart, CURRENT_TIMESTAMP) <= e.eventDate) " +
            "AND (COALESCE(:rangeEnd, '9999-12-31T23:59:59') >= e.eventDate) " +
            "AND (:onlyAvailable IS NULL OR (:onlyAvailable = FALSE " +
            "    OR (e.participantLimit IS NULL AND e.confirmedRequests IS NULL) " +
            "    OR (e.confirmedRequests < e.participantLimit))) " +
            "ORDER BY " +
            "CASE WHEN :sort = 'EVENT_DATE' THEN e.eventDate END ASC, " +
            "CASE WHEN :sort = 'VIEWS' THEN 0 END DESC, " +
            "e.created DESC")
    Page<Event> getAllEvents(@Param("text") String text,
                                  @Param("categories") List<Long> categories,
                                  @Param("paid") Boolean paid,
                                  @Param("rangeStart") LocalDateTime rangeStart,
                                  @Param("rangeEnd") LocalDateTime rangeEnd,
                                  @Param("onlyAvailable") Boolean onlyAvailable,
                                  @Param("sort") String sort,
                                  Pageable pageable);

    List<Event> findAllByInitiatorId(Long initiatorId, Pageable pageable);

    Event findByIdAndInitiatorId(Long userId, Long eventId);
}
