package ru.practicum.explore.ewm.events;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.explore.ewm.common.CommonConstant;
import ru.practicum.explore.ewm.common.OffsetBasedPageRequest;
import ru.practicum.explore.ewm.events.model.Event;
import ru.practicum.explore.ewm.events.model.State;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class EventRepositoryTest {
    @Autowired
    private EventRepository repository;
    Sort sort = Sort.by("id").ascending();

    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void findByUserId_whenOk() {
        List<Event> events = repository.findByUserId(1L, new OffsetBasedPageRequest(0, 10, sort));
        assertEquals(7, events.size());
    }

    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void findByUserId_whenFromSize() {
        List<Event> events = repository.findByUserId(1L, new OffsetBasedPageRequest(1, 1, sort));
        assertEquals(1, events.size());
        assertEquals("title2", events.get(0).getTitle());
        assertEquals("category2", events.get(0).getCategory().getName());
        assertEquals(LocalDateTime.parse("2025-08-01 12:00:00", CommonConstant.FORMATTER), events.get(0).getEventDate());
        assertEquals(38.62f, events.get(0).getLon());
        assertEquals(true, events.get(0).getPaid());
        assertEquals(State.PUBLISHED, events.get(0).getState());
        assertEquals("name1", events.get(0).getInitiator().getName());
    }

    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void findByIdAndInitiatorId_whenOk() {
        Optional<Event> eventOptional = repository.findByIdAndInitiatorId(1L, 1L);
        assertThat(eventOptional).isPresent();
        assertEquals("title1", eventOptional.get().getTitle());
    }

    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void findByIdAndInitiatorId_whenWrongUser() {
        Optional<Event> eventOptional = repository.findByIdAndInitiatorId(1L, 2L);
        assertThat(eventOptional).isEmpty();
    }

    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void findWithConditions_ALL() {
        OffsetBasedPageRequest pageRequest = new OffsetBasedPageRequest(0, 10, Sort.by("eventDate").ascending());
        List<Event> events = repository.findWithConditions(null, List.of(State.PUBLISHED), "newDESC", List.of(1, 2), true,
                LocalDateTime.parse("2025-08-01 12:00:00", CommonConstant.FORMATTER),
                LocalDateTime.parse("2025-08-30 12:00:00", CommonConstant.FORMATTER), pageRequest);
        assertEquals(1, events.size());
        assertEquals("title6", events.get(0).getTitle());
    }

    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void findWithConditions_TextNull() {
        OffsetBasedPageRequest pageRequest = new OffsetBasedPageRequest(0, 10, Sort.by("eventDate").ascending());
        List<Event> events = repository.findWithConditions(null, List.of(State.PUBLISHED), null, List.of(1, 2), true,
                LocalDateTime.parse("2025-08-01 12:00:00", CommonConstant.FORMATTER),
                LocalDateTime.parse("2025-08-30 12:00:00", CommonConstant.FORMATTER), pageRequest);
        assertEquals(1, events.size());
        assertEquals("title6", events.get(0).getTitle());
    }

    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void findWithConditions_Text() {
        List<Event> events = repository.findWithConditions(null, List.of(State.PUBLISHED), "desc", List.of(1), false,
                LocalDateTime.parse("2025-07-02 12:00:00", CommonConstant.FORMATTER),
                null, new OffsetBasedPageRequest(0, 10, Sort.unsorted()));
        assertEquals(1, events.size());
        assertEquals("title3", events.get(0).getTitle());
    }

    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void findWithConditionsAvailableOnly_whenOk() {
        OffsetBasedPageRequest pageRequest = new OffsetBasedPageRequest(0, 10, Sort.by("eventDate").ascending());
        List<Event> events = repository.findWithConditionsAvailableOnly("newDESC", null, null,
                LocalDateTime.parse("2025-08-02 12:00:00", CommonConstant.FORMATTER),
                null, pageRequest);
        assertEquals(3, events.size());
        assertEquals("title6", events.get(0).getTitle());
        assertEquals("title7", events.get(1).getTitle());
        assertEquals("title3", events.get(2).getTitle());
    }

}