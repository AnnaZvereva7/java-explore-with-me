package ru.practicum.explore.ewm.events.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.ewm.events.EventService;
import ru.practicum.explore.ewm.events.dto.EventDto;
import ru.practicum.explore.ewm.events.dto.EventDtoWithAdminComment;
import ru.practicum.explore.ewm.events.dto.EventMapper;
import ru.practicum.explore.ewm.events.dto.request.AdminCommentDto;
import ru.practicum.explore.ewm.events.dto.request.EventDtoRequestUpdateAdmin;
import ru.practicum.explore.ewm.events.dto.request.EventsDtoConfirmation;
import ru.practicum.explore.ewm.events.model.State;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
                                             @RequestParam(defaultValue = "10") @Positive int size) {
        service.checkRangeDate(rangeStart, rangeEnd);
        log.info("Admin: get events from {} size {}, where usersId in:{}, state in:{}, category in:{}, period from {} to {}",
                from, size, users, states, categories, rangeStart, rangeEnd);
        return service.findWithConditionsAdmin(users, states, categories, rangeStart, rangeEnd, from, size)
                .stream()
                .map(mapper::fromEventToDto)
                .collect(Collectors.toList());
    }

    @PatchMapping("/{eventId}")
    public EventDtoWithAdminComment update(@PathVariable Long eventId, @RequestBody @Valid EventDtoRequestUpdateAdmin dto) {
        log.info("Admin: update event with id={}, new info = {}", eventId, dto);
        return mapper.fromEventToDtoWithAdminComment(service.updateByAdmin(dto, eventId));
    }

    @GetMapping(value = "/moderation")
    public List<EventDtoWithAdminComment> getEventForModeration(@RequestParam(required = false) Boolean withAdminComment,
                                                                @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                                @RequestParam(defaultValue = "10") @Positive int size) {
        return service.getEventsForModeration(from, size, withAdminComment)
                .stream()
                .map(mapper::fromEventToDtoWithAdminComment)
                .collect(Collectors.toList());
    }

    @PatchMapping("/moderation")
    public Map<String, List<EventDtoWithAdminComment>> moderateEvents(@RequestBody @Valid EventsDtoConfirmation dto) {
        return service.moderationEvents(dto);
    }

    @PostMapping("/{eventId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public EventDtoWithAdminComment addAdminComment(@RequestBody @Valid AdminCommentDto comment,
                                                    @PathVariable Long eventId) {
        return mapper.fromEventToDtoWithAdminComment(service.addAdminComment(comment, eventId));
    }
}
