package ru.practicum.explore.ewm.requests.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "requests")
@EqualsAndHashCode
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JoinColumn(name = "event_id", nullable = false)
    private Long eventId;
    @JoinColumn(name = "requester_id", nullable = false)
    private Long requesterId;
    private LocalDateTime created;
    @Enumerated(EnumType.STRING)
    private Status status;
}
