package ru.practicum.explore.ewm.compilations.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.ewm.common.Marker;
import ru.practicum.explore.ewm.compilations.CompilationService;
import ru.practicum.explore.ewm.compilations.dto.CompilationDto;
import ru.practicum.explore.ewm.compilations.dto.CompilationDtoRequest;
import ru.practicum.explore.ewm.compilations.dto.CompilationMapper;
import ru.practicum.explore.ewm.events.EventService;
import ru.practicum.explore.ewm.events.model.Event;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/admin/compilations")
@Validated
@RequiredArgsConstructor
@Slf4j
public class CompilationControllerAdmin {

    private final CompilationService service;
    private final CompilationMapper mapper;
    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto addComp(@RequestBody @Validated(Marker.OnCreate.class) CompilationDtoRequest dto) {
        List<Event> events = new ArrayList<>();
        if (!(dto.getEvents() == null || dto.getEvents().isEmpty())) {
            events = eventService.findByIdIn(dto.getEvents());
        }
        return mapper.fromCompilationToDto(service.addComp(mapper.fromDtoRequestToCompilation(dto, events)));
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComp(@PathVariable Long compId) {
        service.deleteComp(compId);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateComp(@PathVariable Long compId, @RequestBody @Validated(Marker.OnUpdate.class) CompilationDtoRequest dto) {
        return mapper.fromCompilationToDto(service.update(compId, dto));
    }
}
