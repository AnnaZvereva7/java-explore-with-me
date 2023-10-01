package ru.practicum.explore.ewm.compilations.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.explore.ewm.compilations.Compilation;
import ru.practicum.explore.ewm.events.dto.EventMapper;
import ru.practicum.explore.ewm.events.model.Event;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CompilationMapper {
    private final EventMapper eventMapper;

    public CompilationDto fromCompilationToDto(Compilation comp) {
        return new CompilationDto(comp.getId(), comp.getTitle(), comp.getPinned(),
                comp.getEventsList().stream().map(eventMapper::fromEventToDtoShort).collect(Collectors.toSet()));
    }

    public Compilation fromDtoRequestToCompilation(CompilationDtoRequest dto, Set<Event> events) {
        return new Compilation(null, dto.getTitle(), dto.getPinned() == null ? false : dto.getPinned(), events);
    }
}
