package ru.practicum.explore.ewm.requests;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explore.ewm.requests.dto.RequestDtoCount;
import ru.practicum.explore.ewm.requests.model.Request;
import ru.practicum.explore.ewm.requests.model.Status;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    @Query(value = "SELECT e.id as event, count(r.id) as requests FROM events e inner JOIN requests r ON r.event_id=e.id " +
            "WHERE r.status='CONFIRMED' and e.id in :ids GROUP BY e.id, r.event_id ORDER BY r.event_id", nativeQuery = true)
    public List<RequestDtoCount> countRequestsByEventId(@Param("ids") List<Long> ids);

    @Query("SELECT r FROM Request r WHERE r.requesterId=:userId AND r.id=:requestId")
    public Optional<Request> findByIdAndUserId(@Param("requestId") Long requestId, @Param("userId") Long userId);

    List<Request> findByRequesterIdOrderById(Long userId);

    List<Request> findByEventIdOrderById(Long eventId);

    List<Request> findByIdInAndStatus(List<Long> ids, Status status);
}
