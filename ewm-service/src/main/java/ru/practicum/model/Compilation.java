package ru.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "compilations")
public class Compilation {

    @Id
    @Column(name = "compilation_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "compilation_title")
    private String title;

    @Column(name = "pinned", nullable = false)
    private Boolean pinned;

    @Column(name = "created")
    private LocalDateTime created;

    @ManyToMany
    @JoinTable(name = "event_compilation",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    List<Event> events;
}
