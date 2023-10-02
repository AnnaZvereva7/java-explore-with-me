package ru.practicum.explore.ewm.compilations;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.explore.ewm.common.OffsetBasedPageRequest;
import ru.practicum.explore.ewm.events.model.Event;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class CompilationRepositoryTest {
    @Autowired
    private CompilationRepository repository;

    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    public void findById() {
        Optional<Compilation> actualComp = repository.findById(4L);
        assertEquals("Compilation4", actualComp.get().getTitle());
        Set<Event> events = actualComp.get().getEventsList();
        List<Event> eventList = new ArrayList<>(events);
        eventList = eventList.stream().sorted(Comparator.comparing(Event::getId)).collect(Collectors.toList());
        assertEquals(3, actualComp.get().getEventsList().size());
        assertEquals("annotation2", eventList.get(0).getAnnotation());
        assertEquals("annotation3", eventList.get(1).getAnnotation());
        assertEquals("annotation6", eventList.get(2).getAnnotation());

    }

    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void findAllWithConditions() {
        List<Compilation> comps = repository.findAllWithConditions(true, new OffsetBasedPageRequest(0, 1, Sort.by("id").ascending()));
        assertEquals(1, comps.size());
        assertEquals("Compilation1", comps.get(0).getTitle());
        assertEquals(3, comps.get(0).getEventsList().size());
        List<Event> events1 = new ArrayList<>(comps.get(0).getEventsList());
        events1 = events1.stream().sorted(Comparator.comparing(Event::getId)).collect(Collectors.toList());
        assertEquals("title2", events1.get(0).getTitle());
        assertEquals("title3", events1.get(1).getTitle());
    }
}