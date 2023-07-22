package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.EventStatus;
import ru.practicum.model.ParticipationRequest;

import java.util.List;
import java.util.Optional;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> findAllByEventIdAndStatus(Long eventId, EventStatus status);

    List<ParticipationRequest> findAllByRequestorId(Long requestorId);

    Optional<ParticipationRequest> findByIdAndRequestorId(Long id, Long requestorId);

    List<ParticipationRequest> findAllByRequestor_IdAndEvent_IdAndIdIn(
            Long requestorId, Long eventId, List<Integer> requestIds);

    @Query(value = "SELECT COUNT(*) " +
            "FROM participation_requests " +
            "WHERE event_id = ?1 and status = 'CONFIRMED'", nativeQuery = true)
    Integer getConfirmedRequestsByEventId(Long eventId);

    List<ParticipationRequest> findAllByEventId(Long eventId);
}
