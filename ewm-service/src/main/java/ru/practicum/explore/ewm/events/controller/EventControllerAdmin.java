package ru.practicum.explore.ewm.events.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.ewm.common.Marker;
import ru.practicum.explore.ewm.events.EventService;
import ru.practicum.explore.ewm.events.dto.EventDto;
import ru.practicum.explore.ewm.events.dto.EventDtoRequest;
import ru.practicum.explore.ewm.events.dto.EventMapper;
import ru.practicum.explore.ewm.events.model.State;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Validated
@Slf4j
public class EventControllerAdmin {
    private final EventService service;
    private final EventMapper mapper;

    @GetMapping
    public List<EventDto> findWithConditions(@RequestParam(required = false) List<Long> users,
                                             @RequestParam(required = false) List<State> states,
                                             @RequestParam(required = false) List<Integer> categories,
                                             @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                             @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                             @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                             @RequestParam(defaultValue = "10") @Positive int size) throws UnsupportedEncodingException {
        service.checkRangeDate(rangeStart, rangeEnd);
        log.info("Admin: get events from {} size {}, where usersId in:{}, state in:{}, category in:{}, period from {} to {}",
                from, size, users, states, categories, rangeStart, rangeEnd);
        return service.findWithConditionsAdmin(users, states, categories, rangeStart, rangeEnd, from, size)
                .stream()
                .map(mapper::fromEventToDto)
                .collect(Collectors.toList());
    }

    @PatchMapping("/{eventId}")
    public EventDto update(@PathVariable Long eventId, @RequestBody @Validated(Marker.OnUpdate.class) EventDtoRequest dto) throws UnsupportedEncodingException {
        log.info("Admin: update event with id={}, new info = {}", eventId, dto);
        return mapper.fromEventToDto(service.updateByAdmin(dto, eventId));
    }
}
