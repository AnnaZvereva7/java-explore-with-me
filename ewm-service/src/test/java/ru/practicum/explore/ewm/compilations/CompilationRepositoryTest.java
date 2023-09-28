package ru.practicum.explore.ewm.compilations;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.explore.ewm.common.OffsetBasedPageRequest;

import java.util.List;
import java.util.Optional;

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
        assertEquals(3, actualComp.get().getEventsList().size());
        assertEquals("annotation2", actualComp.get().getEventsList().get(0).getAnnotation());
        assertEquals("annotation3", actualComp.get().getEventsList().get(1).getAnnotation());
        assertEquals("annotation7", actualComp.get().getEventsList().get(2).getAnnotation());
    }

    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void findAllWithConditions() {
        List<Compilation> comps = repository.findAllWithConditions(true, new OffsetBasedPageRequest(0, 1, Sort.by("id").ascending()));
        assertEquals(1, comps.size());
        assertEquals("Compilation1", comps.get(0).getTitle());
        assertEquals(3, comps.get(0).getEventsList().size());
        assertEquals("title2", comps.get(0).getEventsList().get(0).getTitle());
        assertEquals("title3", comps.get(0).getEventsList().get(1).getTitle());
    }
}