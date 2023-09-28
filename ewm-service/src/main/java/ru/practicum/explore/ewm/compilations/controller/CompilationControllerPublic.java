package ru.practicum.explore.ewm.compilations.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.ewm.compilations.CompilationService;
import ru.practicum.explore.ewm.compilations.dto.CompilationDto;
import ru.practicum.explore.ewm.compilations.dto.CompilationMapper;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/compilations")
@Validated
@RequiredArgsConstructor
@Slf4j
public class CompilationControllerPublic {

    private final CompilationService service;
    private final CompilationMapper mapper;

    @GetMapping
    public List<CompilationDto> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                @RequestParam(defaultValue = "10") @Positive int size) {
        return service.findCompListWithConditions(pinned, from, size)
                .stream()
                .map(mapper::fromCompilationToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilationById(@PathVariable Long compId) {
        return mapper.fromCompilationToDto(service.findComp(compId));
    }
}
