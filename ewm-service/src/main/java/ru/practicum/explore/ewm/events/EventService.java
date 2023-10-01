package ru.practicum.explore.ewm.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.explore.ewm.categories.CategoryService;
import ru.practicum.explore.ewm.common.OffsetBasedPageRequest;
import ru.practicum.explore.ewm.events.dto.EventDtoRequest;
import ru.practicum.explore.ewm.events.model.Event;
import ru.practicum.explore.ewm.events.model.EventSort;
import ru.practicum.explore.ewm.events.model.State;
import ru.practicum.explore.ewm.events.model.StateAction;
import ru.practicum.explore.ewm.exceptions.*;
import ru.practicum.explore.ewm.requests.RequestRepository;
import ru.practicum.explore.ewm.requests.dto.RequestDto;
import ru.practicum.explore.ewm.requests.dto.RequestDtoConfirmation;
import ru.practicum.explore.ewm.requests.dto.RequestDtoCount;
import ru.practicum.explore.ewm.requests.dto.RequestMapper;
import ru.practicum.explore.ewm.requests.model.Request;
import ru.practicum.explore.ewm.requests.model.Status;
import ru.practicum.explore.stats.client.StatsClient;
import ru.practicum.explore.stats.dto.StatisticDto;
import ru.practicum.explore.stats.dto.StatsMapper;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {
    private final StatsClient client;
    private final EventRepository repository;
    private final CategoryService categoryService;
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;
    private final StatsMapper statsMapper;
    private Sort sort = Sort.by("id").ascending();

    public Event addEvent(Event event) {
        return repository.saveAndFlush(event);
    }

    public List<Event> findByUserId(Long userId, int from, int size) {
        List<Event> events = repository.findByUserId(userId, new OffsetBasedPageRequest(from, size, sort));
        return addViewsAndCountRequests(events);
    }

    public Event findEventByIdAndUserFull(Long eventId, Long userId) {
        Event event = repository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        return addViewsAndCountRequests(event);
    }

    public Event updateByOwner(EventDtoRequest dto, Long userId, Long eventId) {
        Event event = repository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        if (event.getState() == State.PUBLISHED) {
            throw new AccessException("Event must not be published");
        }
        event = updateInformation(dto, event);
        if (dto.getStateAction() != null && dto.getStateAction().equals(StateAction.CANCEL_REVIEW) && event.getState().equals(State.PENDING)) {
            event.setState(State.CANCELED);
        } else if (dto.getStateAction() != null && dto.getStateAction().equals(StateAction.SEND_TO_REVIEW) && event.getState().equals(State.CANCELED)) {
            checkEvenDate(event.getEventDate(), 2);
            event.setState(State.PENDING);
        }
        return repository.saveAndFlush(event);
    }

    public Event updateByAdmin(EventDtoRequest dto, Long eventId) {
        log.info("update {}", dto);
        Event event = findByIdAllState(eventId);
        event = updateInformation(dto, event);
        if (dto.getStateAction() == null) {
            return repository.saveAndFlush(event);
        } else if (dto.getStateAction().equals(StateAction.PUBLISH_EVENT) && event.getState().equals(State.PENDING)) {
            checkEvenDate(event.getEventDate(), 1);
            event.setPublishedOn(LocalDateTime.now());
            event.setState(State.PUBLISHED);
            return repository.saveAndFlush(event);
        } else if (dto.getStateAction().equals(StateAction.REJECT_EVENT) && event.getState().equals(State.PENDING)) {
            event.setState(State.CANCELED);
            return event = repository.saveAndFlush(event);
        } else {
            throw new AccessException("Cannot publish the event");
        }
    }

    private Event updateInformation(EventDtoRequest dto, Event event) {
        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
            event.setTitle(dto.getTitle());
        }
        if (dto.getAnnotation() != null && !dto.getAnnotation().isBlank()) {
            event.setAnnotation(dto.getAnnotation());
        }
        if (dto.getDescription() != null && !dto.getDescription().isBlank()) {
            event.setDescription(dto.getDescription());
        }
        if (dto.getCategoryId() != null) {
            event.setCategory(categoryService.findById(dto.getCategoryId()));
        }
        if (dto.getEventDate() != null) {
            checkEvenDate(dto.getEventDate(), 2);
            event.setEventDate(dto.getEventDate());
        }
        if (dto.getLocationDto() != null) {
            event.setLat(dto.getLocationDto().getLat());
            event.setLon(dto.getLocationDto().getLon());
        }
        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }
        if (dto.getParticipantLimit() != null) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }
        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }
        return event;
    }

    public void checkEvenDate(LocalDateTime eventDate, int hoursBefore) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(hoursBefore))) {
            throw new EventDateException(eventDate);
        }
    }

    public List<Event> findWithConditions(String text, List<Integer> categories, Boolean paid, LocalDateTime rangeStart,
                                          LocalDateTime rangeEnd, boolean onlyAvailable, EventSort sort, int from, int size) {
        List<Event> events;
        Sort sorted;
        if (sort == null || sort.equals(EventSort.VIEWS)) {
            sorted = Sort.unsorted();
        } else {
            sorted = Sort.by("eventDate").ascending();
        }
        if (!onlyAvailable) {
            events = repository.findWithConditions(null, List.of(State.PUBLISHED), text, categories, paid, rangeStart, rangeEnd, new OffsetBasedPageRequest(from, size, sorted));
        } else {
            events = repository.findWithConditionsAvailableOnly(text, categories, paid, rangeStart, rangeEnd, new OffsetBasedPageRequest(from, size, sorted));
        }
        if (sort == null || sort.equals(EventSort.EVENT_DATE)) {
            return addViewsAndCountRequests(events);
        } else {
            return addViewsAndCountRequests(events)
                    .stream()
                    .sorted(Comparator.comparing(Event::getViews))
                    .collect(Collectors.toList());
        }
    }

    public List<Event> findWithConditionsAdmin(List<Long> users, List<State> states, List<Integer> categories, LocalDateTime rangeStart,
                                               LocalDateTime rangeEnd, int from, int size) {
        List<Event> events = repository.findWithConditions(users, states, null, categories, null, rangeStart, rangeEnd, new OffsetBasedPageRequest(from, size, Sort.by("id").ascending()));
        return addViewsAndCountRequests(events);
    }

    public Event findByIdAllState(Long id) {
        Event event = repository.findById(id).orElseThrow(() -> new NotFoundException("Event with id=" + id + " was not found"));
        return addViewsAndCountRequests(event);
    }

    public Event findByIdPublished(Long id) {
        Event event = repository.findByIdAndState(id, State.PUBLISHED).orElseThrow(() -> new NotFoundException("Event with id=" + id + " was not found"));
        return addViewsAndCountRequests(event);
    }

    public Event findByIdAndUserWithOutStatistic(Long eventId, Long userId) {
        Event event = repository.findById(eventId).orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        Map<Long, Integer> confirmedRequests = requestRepository.countRequestsByEventId(List.of(event.getId())).stream()
                .collect(Collectors.toMap(RequestDtoCount::getEvent, RequestDtoCount::getRequests));
        event.setConfirmedRequests(confirmedRequests.getOrDefault(event.getId(), 0));
        return event;
    }

    public List<Event> findByIdIn(List<Long> ids) {
        return addViewsAndCountRequests(repository.findByIdIn(ids));
    }

    public Set<Event> findByIdInSet(Set<Long> ids) {
        List<Event> events = new ArrayList<>(repository.findByIdIn(ids));
        return new HashSet<>(addViewsAndCountRequests(events));
    }

    public List<Event> addViewsAndCountRequests(List<Event> events) {
        if (events == null || events.isEmpty()) {
            return events;
        } else {
            LocalDateTime minDate = null;
            for (Event event : events) {
                if (event.getPublishedOn() != null) {
                    if (minDate != null && minDate.isAfter(event.getPublishedOn())) {
                        minDate = event.getPublishedOn();
                    } else if (minDate == null) {
                        minDate = event.getPublishedOn();
                    }
                }
            }
            Map<Long, Integer> statisticMap = new HashMap<>();
            Map<Long, Integer> confirmedRequests = new HashMap<>();
            if (minDate != null) {
                List<Long> ids = events.stream().map(Event::getId).collect(Collectors.toList());
                statisticMap = getStatisticAsMap(ids, true, minDate);
                confirmedRequests = requestRepository.countRequestsByEventId(ids).stream()
                        .collect(Collectors.toMap(RequestDtoCount::getEvent, RequestDtoCount::getRequests));

            }
            for (Event event : events) {
                event.setViews(statisticMap.getOrDefault(event.getId(), 0));
                event.setConfirmedRequests(confirmedRequests.getOrDefault(event.getId(), 0));
            }
            return events;
        }
    }

    private Event addViewsAndCountRequests(Event event) {
        if (event == null) {
            return event;
        }
        if (event.getPublishedOn() == null) {
            event.setViews(0);
            event.setConfirmedRequests(0);
        } else {
            Map<Long, Integer> statistic = getStatisticAsMap(List.of(event.getId()), true, event.getPublishedOn());
            Map<Long, Integer> confirmedRequests = requestRepository.countRequestsByEventId(List.of(event.getId())).stream()
                    .collect(Collectors.toMap(RequestDtoCount::getEvent, RequestDtoCount::getRequests));
            event.setViews(statistic.getOrDefault(event.getId(), 0));
            event.setConfirmedRequests(confirmedRequests.getOrDefault(event.getId(), 0));
        }
        return event;
    }

    private Map<Long, Integer> getStatisticAsMap(List<Long> ids, Boolean unique, LocalDateTime startDate) {
        List<String> uris = ids.stream().map(k -> "/events/" + k).collect(Collectors.toList());
        try {
            List<StatisticDto> statistic = client.get(startDate,
                    LocalDateTime.now(), uris, unique);
            return statistic.stream().collect(Collectors.toMap(statsMapper::getEventId, StatisticDto::getHits));
        } catch (UnsupportedEncodingException e) {
            throw new GettingStatisticException(e.getMessage());
        }
    }

    public List<Request> findRequestsByEventId(Long eventId, Long userId) {
        repository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        return requestRepository.findByEventIdOrderById(eventId);
    }

    public Map<String, List<RequestDto>> requestConfirmation(RequestDtoConfirmation confirmationDto, Long userId, Long eventId) {
        Event event = findByIdAndUserWithOutStatistic(eventId, userId);
        List<Request> requests = requestRepository.findByIdInAndStatus(confirmationDto.getRequestIds(), Status.PENDING);
        if (requests.size() != confirmationDto.getRequestIds().size()) {
            throw new AccessException("Requests must have status PENDING");
        }
        if (!checkLimitForRequest(event.getParticipantLimit(), event.getConfirmedRequests())) {
            throw new AccessException("The participant limit has been reached");
        }
        Map<String, List<RequestDto>> resultMap = new HashMap<>();
        List<Request> confirmedRequests = new ArrayList<>();
        List<Request> rejectedRequests = new ArrayList<>();
        if (confirmationDto.getStatus().equals(RequestDtoConfirmation.StatusConfirmation.CONFIRMED)) {
            for (Request request : requests) {
                if (checkLimitForRequest(event.getParticipantLimit(), event.getConfirmedRequests())) {
                    request.setStatus(Status.CONFIRMED);
                    confirmedRequests.add(requestRepository.saveAndFlush(request));
                } else {
                    request.setStatus(Status.REJECTED);
                    rejectedRequests.add(requestRepository.saveAndFlush(request));
                }
            }
        } else if (confirmationDto.getStatus().equals(RequestDtoConfirmation.StatusConfirmation.REJECTED)) {
            for (Request request : requests) {
                request.setStatus(Status.REJECTED);
                rejectedRequests.add(requestRepository.saveAndFlush(request));
            }
        } else {
            throw new WrongStatusException("Заявки можно только одобрить или отклонить");
        }
        resultMap.put("confirmedRequests", confirmedRequests.stream().map(requestMapper::fromRequestToDto).collect(Collectors.toList()));
        resultMap.put("rejectedRequests", rejectedRequests.stream().map(requestMapper::fromRequestToDto).collect(Collectors.toList()));
        return resultMap;
    }

    private boolean checkLimitForRequest(Integer limit, Integer confirmedRequest) {
        if (confirmedRequest == null) {
            confirmedRequest = 0;
        }
        if (limit == 0) {
            return true;
        } else return limit - confirmedRequest > 0;
    }

    public void checkRangeDate(LocalDateTime start, LocalDateTime end) {
        if (start != null & end != null && !start.isBefore(end)) {
            throw new WrongParamException("end before start");
        }
    }

}
