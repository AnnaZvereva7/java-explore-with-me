package ru.practicum.explore.ewm.events.model;

import lombok.*;
import ru.practicum.explore.ewm.categories.Category;
import ru.practicum.explore.ewm.compilations.Compilation;
import ru.practicum.explore.ewm.users.User;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "events")
@EqualsAndHashCode
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String annotation;
    @Column(nullable = false)
    private String description;
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    @Column(nullable = false, name = "event_date")
    private LocalDateTime eventDate;
    @Column(nullable = false)
    private BigDecimal lat;
    @Column(nullable = false)
    private BigDecimal lon;
    @Column(nullable = false)
    private Boolean paid;
    @Column(name = "participant_limit")
    private Integer participantLimit;
    @Column(nullable = false, name = "request_moderation")
    private Boolean requestModeration;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private State state;
    @ManyToOne
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;
    @Column(nullable = false, name = "created_on")
    private LocalDateTime createdOn;
    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @ManyToMany(mappedBy = "eventsList", fetch = FetchType.LAZY)
    @Transient
    @EqualsAndHashCode.Exclude
    private List<Compilation> compilations;
    @Column(name = "admin_comment")
    private String adminComment;

    @Transient
    @EqualsAndHashCode.Exclude
    private Integer confirmedRequests;
    @Transient
    @EqualsAndHashCode.Exclude
    private Integer views;
}
