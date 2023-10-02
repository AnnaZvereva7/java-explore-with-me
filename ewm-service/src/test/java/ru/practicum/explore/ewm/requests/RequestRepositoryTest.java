package ru.practicum.explore.ewm.requests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.explore.ewm.common.CommonConstant;
import ru.practicum.explore.ewm.requests.dto.RequestDtoCount;
import ru.practicum.explore.ewm.requests.model.Request;
import ru.practicum.explore.ewm.requests.model.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class RequestRepositoryTest {
    @Autowired
    RequestRepository repository;

    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void countRequestsByEventId() {
        List<RequestDtoCount> actualList = repository.countRequestsByEventId(List.of(1L, 2L, 3L));
        assertEquals(2, actualList.size());
        assertEquals(1L, actualList.get(0).getEvent());
        assertEquals(2, actualList.get(0).getRequests());
        assertEquals(2L, actualList.get(1).getEvent());
        assertEquals(1, actualList.get(1).getRequests());
    }

    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void findByIdAndUserId_whenOk() {
        Optional<Request> request = repository.findByIdAndUserId(1L, 2L);
        assertThat(request).isPresent();
        assertEquals(1L, request.get().getEventId());
        assertEquals(Status.CONFIRMED, request.get().getStatus());
        assertEquals(LocalDateTime.parse("2020-08-08 12:00:00", CommonConstant.FORMATTER), request.get().getCreated());
    }

    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void findByIdAndUserId_whenEmpty() {
        Optional<Request> request = repository.findByIdAndUserId(2L, 2L);
        assertThat(request).isEmpty();
    }

    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void findByRequesterId() {
        List<Request> requests = repository.findByRequesterIdOrderById(2L);
        assertEquals(2, requests.size());
        assertEquals(1L, requests.get(0).getId());
        assertEquals(4L, requests.get(1).getId());
    }

}