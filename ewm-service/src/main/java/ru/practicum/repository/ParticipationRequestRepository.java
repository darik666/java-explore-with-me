package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.enums.EventStatus;
import ru.practicum.model.ParticipationRequest;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий запросов на участие в событии
 */
public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> findAllByEventIdAndStatus(Long eventId, EventStatus status);

    List<ParticipationRequest> findAllByRequestorId(Long requestorId);

    Optional<ParticipationRequest> findByIdAndRequestorId(Long id, Long requestorId);

    @Query("SELECT pr FROM ParticipationRequest pr " +
            "WHERE pr.event.id = :eventId " +
            "AND pr.id IN (:requestIds)")
    List<ParticipationRequest> findAllByEventIdAndIdIn(
            @Param("eventId") Long eventId,
            @Param("requestIds") List<Long> requestIds);

    @Query(value = "SELECT COUNT(*) " +
            "FROM participation_requests " +
            "WHERE event_id = ?1 and status = 'CONFIRMED'", nativeQuery = true)
    Integer getConfirmedRequestsByEventId(Long eventId);

    List<ParticipationRequest> findAllByEventId(Long eventId);

    ParticipationRequest findByEventIdAndRequestorId(Long eventId, Long requestorId);
}