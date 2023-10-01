package ru.practicum.explore.ewm.events.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.ewm.events.EventService;
import ru.practicum.explore.ewm.events.dto.EventDto;
import ru.practicum.explore.ewm.events.dto.EventDtoShort;
import ru.practicum.explore.ewm.events.dto.EventMapper;
import ru.practicum.explore.ewm.events.model.EventSort;
import ru.practicum.explore.stats.client.StatsClient;
import ru.practicum.explore.stats.dto.HitDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Validated
@Slf4j
public class EventControllerPublic {
    private final EventService service;
    private final EventMapper mapper;
    private final StatsClient client;
    @Value("${ewm.service.app.name}")
    private String appName;

    @GetMapping
    public List<EventDtoShort> findWithConditions(@RequestParam(required = false) String text,
                                                  @RequestParam(required = false) List<Integer> categories,
                                                  @RequestParam(required = false) Boolean paid,
                                                  @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                  @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                  @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                                  @RequestParam(required = false) EventSort sort,
                                                  @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                  @RequestParam(defaultValue = "10") @Positive int size,
                                                  HttpServletRequest request) {
        log.info("Public: client ip: {}, appName={}", request.getRemoteAddr(), appName);
        client.post(new HitDto(null, appName, "/events", request.getRemoteAddr(), LocalDateTime.now()));
        log.info("Public: get events from {} size {}, where text={}, category in:{}, paid={}, event date between ({} - {}), only available={}, sort by {}",
                from, size, text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort);
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        service.checkRangeDate(rangeStart, rangeEnd);
        log.info("find with conditions text {}, categories {}, paid {}, start {}, end {}, onlyAvailable {}, sort {}, from {}, size {}", text, categories,
                paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        return service.findWithConditions(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size)
                .stream()
                .map(mapper::fromEventToDtoShort)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public EventDto getById(@PathVariable Long id, HttpServletRequest request) {
        log.info("Public: client ip: {}, appName={}", request.getRemoteAddr(), appName);
        client.post(new HitDto(null, appName, "/events/" + id, request.getRemoteAddr(), LocalDateTime.now()));
        log.info("Public: get event with id={}", id);
        return mapper.fromEventToDto(service.findByIdPublished(id));
    }

}
