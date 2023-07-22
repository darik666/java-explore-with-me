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

    @Query("SELECT e " +
            "FROM Event e " +
            "WHERE ((LOWER(e.annotation) LIKE CONCAT('%',lower(:text),'%') " +
            "OR LOWER(e.description) LIKE CONCAT('%',lower(:text),'%')) " +
            "OR :text IS NULL ) " +
            "AND (e.category.id IN (:categories) OR :categories IS NULL) " +
            "AND (e.paid IN (:paid) OR :paid IS NULL) " +
            "AND (e.eventDate BETWEEN :rangeStart AND :rangeEnd) " +
            "AND (:onlyAvailable = TRUE " +
            "AND (e.confirmedRequests < e.participantLimit OR (e.confirmedRequests IS NULL " +
            "AND e.participantLimit IS NOT NULL)) " +
            "OR (:onlyAvailable = FALSE))")
    List<Event> findAllEvents(@Param("text") String text,
                              @Param("categories") List<Long> categories,
                              @Param("paid") Boolean paid,
                              @Param("rangeStart") LocalDateTime rangeStart,
                              @Param("rangeEnd") LocalDateTime rangeEnd,
                              @Param("onlyAvailable") Boolean onlyAvailable,
                              Pageable pageable);

    List<Event> findAllByInitiatorId(Long initiatorId, Pageable pageable);

    Event findByIdAndInitiatorId(Long userId, Long eventId);
}
