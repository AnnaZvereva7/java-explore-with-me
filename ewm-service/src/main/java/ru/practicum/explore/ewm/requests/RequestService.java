package ru.practicum.explore.ewm.requests;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.explore.ewm.events.EventService;
import ru.practicum.explore.ewm.events.model.Event;
import ru.practicum.explore.ewm.events.model.State;
import ru.practicum.explore.ewm.exceptions.AccessException;
import ru.practicum.explore.ewm.exceptions.NotFoundException;
import ru.practicum.explore.ewm.requests.model.Request;
import ru.practicum.explore.ewm.requests.model.Status;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RequestService {
    private final RequestRepository repository;
    private final EventService eventService;

    public Request findByIdAndUser(Long requestId, Long userId) {
        return repository.findByIdAndUserId(requestId, userId).orElseThrow(() -> new NotFoundException("Request with id=" + requestId + " was not found"));
    }

    public Request cancelRequest(Long requestId, Long userId) {
        Request request = findByIdAndUser(requestId, userId);
        if (request.getStatus().equals(Status.CONFIRMED)) {
            throw new AccessException("Нельзя отменить подтвержденную заявку");
        }
        request.setStatus(Status.CANCELED);
        return repository.saveAndFlush(request);
    }

    @Transactional
    public Request addrequest(Long userId, Long eventId) {
        Event event = eventService.findByIdAndUserWithOutStatistic(eventId, userId);
        if (Objects.equals(userId, event.getInitiator().getId())) {
            throw new AccessException("Нельзя подавать заявку на свое мероприятие");
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new AccessException("Нельзя участвовать в неопубликованном мероприятии");
        }
        if (!checkLimitForRequest(event.getParticipantLimit(), event.getConfirmedRequests())) {
            throw new AccessException("Нельзя превысить лимит участников мероприятия");
        }
        Request newRequest = new Request(null, eventId, userId, LocalDateTime.now(), null);
        if (event.getRequestModeration() && event.getParticipantLimit() != 0) {
            newRequest.setStatus(Status.PENDING);
        } else newRequest.setStatus(Status.CONFIRMED);
        return repository.saveAndFlush(newRequest);
    }

    private boolean checkLimitForRequest(Integer limit, Integer confirmedRequest) {
        if (confirmedRequest == null) {
            confirmedRequest = 0;
        }
        if (limit == null || limit == 0) {
            return true;
        } else return limit - confirmedRequest > 0;
    }

    public List<Request> findByRequesterId(Long userId) {
        return repository.findByRequesterIdOrderById(userId);
    }

}
