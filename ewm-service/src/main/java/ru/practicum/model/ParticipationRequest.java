package ru.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.EventStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "participation_requests")
public class ParticipationRequest {

    @Id
    @Column(name = "participation_request_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "requestor_id")
    private User requestor;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private EventStatus status;

    @Column(name = "created")
    private LocalDateTime created;
}
