package ru.practicum.explore.ewm.compilations;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.explore.ewm.common.OffsetBasedPageRequest;
import ru.practicum.explore.ewm.compilations.dto.CompilationDtoRequest;
import ru.practicum.explore.ewm.events.EventService;
import ru.practicum.explore.ewm.events.model.Event;
import ru.practicum.explore.ewm.exceptions.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompilationService {
    private final CompilationRepository repository;
    private final EventService eventService;

    public Compilation addComp(Compilation comp) {
        return repository.saveAndFlush(comp);
    }

    public Compilation findComp(Long id) {
        Compilation comp = repository.findById(id).orElseThrow(() -> new NotFoundException("Compilation with id=" + id + " was not found"));
        List<Event> events = eventService.addViewsAndCountRequests(comp.getEventsList());
        comp.setEventsList(events);
        return comp;
    }

    public List<Compilation> findCompListWithConditions(Boolean pinned, int from, int size) {
        return repository.findAllWithConditions(pinned, new OffsetBasedPageRequest(from, size, Sort.by("id").ascending()));
    }

    public void deleteComp(Long compId) {
        repository.findById(compId).orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));
        repository.deleteById(compId);
    }

    public Compilation update(Long compId, CompilationDtoRequest dto) { //todo tests при удалении, добавлении ивента из списка. названия пустого или отсутствующего
        Compilation comp = findComp(compId);
        if (dto.getEvents() != null) comp = updateEventList(comp, dto);
        if (!(dto.getTitle() == null || dto.getTitle().isBlank())) comp.setTitle(dto.getTitle());
        if (dto.getPinned() != null) comp.setPinned(dto.getPinned());
        return repository.saveAndFlush(comp);
    }

    private Compilation updateEventList(Compilation comp, CompilationDtoRequest dto) {
        if (dto.getEvents() == null || dto.getEvents().isEmpty()) {
            comp.setEventsList(List.of());
        } else {
            List<Long> eventIdsNew = dto.getEvents();
            for (Event event : comp.getEventsList()) {
                if (eventIdsNew.contains(event.getId())) {
                    eventIdsNew.remove(event.getId());
                } else {
                    comp.getEventsList().remove(event);
                }
            }
            if (!eventIdsNew.isEmpty()) {
                List<Event> addition = eventService.findByIdIn(eventIdsNew);
                comp.getEventsList().addAll(addition);
            }
        }
        return comp;
    }
}
