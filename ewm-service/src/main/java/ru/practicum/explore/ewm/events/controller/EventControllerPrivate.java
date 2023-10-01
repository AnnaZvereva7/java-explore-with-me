package ru.practicum.explore.ewm.events.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.ewm.categories.Category;
import ru.practicum.explore.ewm.categories.CategoryService;
import ru.practicum.explore.ewm.common.Marker;
import ru.practicum.explore.ewm.events.EventService;
import ru.practicum.explore.ewm.events.dto.EventDto;
import ru.practicum.explore.ewm.events.dto.EventDtoRequest;
import ru.practicum.explore.ewm.events.dto.EventDtoShort;
import ru.practicum.explore.ewm.events.dto.EventMapper;
import ru.practicum.explore.ewm.events.model.Event;
import ru.practicum.explore.ewm.requests.dto.RequestDto;
import ru.practicum.explore.ewm.requests.dto.RequestDtoConfirmation;
import ru.practicum.explore.ewm.requests.dto.RequestMapper;
import ru.practicum.explore.ewm.users.User;
import ru.practicum.explore.ewm.users.UserService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users/{userId}/events")
@Validated
@RequiredArgsConstructor
@Slf4j
public class EventControllerPrivate {
    private final EventService service;
    private final EventMapper mapper;
    private final RequestMapper requestMapper;
    private final UserService userService;
    private final CategoryService categoryService;

    @GetMapping
    public List<EventDtoShort> getEventsByUser(@PathVariable Long userId,
                                               @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                               @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Private: get list of event with initiator.id {} from{} size{}", userId, from, size);
        return service.findByUserId(userId, from, size)
                .stream()
                .map(mapper::fromEventToDtoShort)
                .collect(Collectors.toList());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto addEvent(@PathVariable Long userId,
                             @RequestBody @Validated(Marker.OnCreate.class) EventDtoRequest dto) {
        log.info("Private: add new event {} by user {}", dto, userId);
        service.checkEvenDate(dto.getEventDate(), 2);
        User initiator = userService.findById(userId);
        Category category = categoryService.findById(dto.getCategoryId());
        Event newEvent = mapper.fromDtoRequestToNewEvent(dto, initiator, category);
        return mapper.fromEventToDto(service.addEvent(newEvent));
    }

    @GetMapping("/{eventId}")
    public EventDto getEventById(@PathVariable Long eventId,
                                 @PathVariable Long userId) {
        log.info("Private: get event with id {} by user with id {}", eventId, userId);
        return mapper.fromEventToDto(service.findEventByIdAndUserFull(eventId, userId));
    }

    @PatchMapping("/{eventId}")
    public EventDto updateEvent(@PathVariable Long eventId,
                                @PathVariable Long userId,
                                @RequestBody @Validated(Marker.OnUpdate.class) EventDtoRequest dto) {
        log.info("Private: update information about event id {} by user.id={}, new information {}", eventId, userId, dto);
        return mapper.fromEventToDto(service.updateByOwner(dto, userId, eventId));
    }

    @GetMapping("/{eventId}/requests")
    public List<Object> getRequestsByEventId(@PathVariable Long eventId,
                                             @PathVariable Long userId) {
        log.info("Private: get all request for event id {}", eventId);
        return service.findRequestsByEventId(eventId, userId)
                .stream()
                .map(requestMapper::fromRequestToDto)
                .collect(Collectors.toList());
    }

    @PatchMapping("/{eventId}/requests")
    public Map<String, List<RequestDto>> moderateRequests(@PathVariable Long eventId,
                                                          @PathVariable Long userId,
                                                          @RequestBody RequestDtoConfirmation confirmationDto) {
        log.info("Private: moderation requests vent.id ={}, user.id={}, confirmationDto={}", eventId, userId, confirmationDto);
        return service.requestConfirmation(confirmationDto, userId, eventId);
    }
}


