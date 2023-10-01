package ru.practicum.explore.ewm.events;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explore.ewm.common.OffsetBasedPageRequest;
import ru.practicum.explore.ewm.events.model.Event;
import ru.practicum.explore.ewm.events.model.State;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("SELECT e from Event e JOIN FETCH e.initiator i JOIN FETCH e.category c WHERE i.id=:userId")
    List<Event> findByUserId(@Param("userId") Long userId, OffsetBasedPageRequest pageRequest);

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long userId);

    @Query("SELECT e from Event e JOIN FETCH e.category c JOIN FETCH e.initiator i WHERE " +
            "(:text IS NULL OR LOWER(e.annotation) LIKE LOWER(CONCAT('%',:text,'%')) OR LOWER(e.description) LIKE LOWER(CONCAT('%',:text,'%'))) " +
            "AND ((:categories) IS NULL OR c.id IN (:categories)) " +
            "AND ((:users) IS NULL OR i.id IN (:users)) " +
            "AND ((:states) IS NULL OR e.state IN (:states)) " +
            "AND (:paid IS NULL OR e.paid=:paid) " +
            "AND (cast(:rangeEnd as date) IS NULL OR e.eventDate<:rangeEnd) " +
            "AND (cast(:rangeStart as date) IS NULL OR e.eventDate>:rangeStart)")
    List<Event> findWithConditions(@Param("users") List<Long> users,
                                   @Param("states") List<State> states,
                                   @Param("text") String text,
                                   @Param("categories") List<Integer> categories,
                                   @Param("paid") Boolean paid,
                                   @Param("rangeStart") LocalDateTime rangeStart,
                                   @Param("rangeEnd") LocalDateTime rangeEnd,
                                   OffsetBasedPageRequest pageRequest);

    @Query("SELECT e from Event e JOIN FETCH e.category c WHERE " +
            "(:text IS NULL OR LOWER(e.annotation) LIKE LOWER(CONCAT('%',:text,'%')) OR LOWER(e.description) LIKE LOWER(CONCAT('%',:text,'%'))) " +
            "AND ((:categories) IS NULL OR c.id IN (:categories)) " +
            "AND (:paid IS NULL OR e.paid=:paid) " +
            "AND (:rangeEnd IS NULL OR e.eventDate<:rangeEnd) " +
            "AND e.eventDate>:rangeStart AND e.state='PUBLISHED' " +
            "AND ((SELECT count(r.id) from Request r WHERE r.eventId=e.id AND r.status='CONFIRMED')<e.participantLimit " +
            "OR (SELECT count(r.id) from Request r WHERE r.eventId=e.id AND r.status='CONFIRMED')=0)")
    List<Event> findWithConditionsAvailableOnly(@Param("text") String text,
                                                @Param("categories") List<Integer> categories,
                                                @Param("paid") Boolean paid,
                                                @Param("rangeStart") LocalDateTime rangeStart,
                                                @Param("rangeEnd") LocalDateTime rangeEnd,
                                                OffsetBasedPageRequest pageRequest);

    List<Event> findByIdIn(List<Long> ids);

    Set<Event> findByIdIn(Set<Long> ids);

    Optional<Event> findByIdAndState(Long id, State state);
}
