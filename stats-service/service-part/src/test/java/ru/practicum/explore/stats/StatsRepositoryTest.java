package ru.practicum.explore.stats;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.explore.stats.dto.StatisticDtoInterface;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class StatsRepositoryTest {

    @Autowired
    StatsRepository repository;
    private LocalDateTime now = LocalDateTime.of(2022, 1, 1, 12, 0, 0);

    @Test
    @Sql({"/schema_test.sql"})
    void getStatisticUnique_whenEmpty() {
        List<StatisticDtoInterface> statistic = repository.getStatisticUnique(now, now.plusMonths(12), List.of("/events/1"));
        assertEquals(0, statistic.size());
    }

    @Test
    @Sql({"/schema_test.sql", "/import_table.sql"})
    void getStatisticUnique() {
        List<StatisticDtoInterface> statistic = repository.getStatisticUnique(now, now.plusMonths(12), List.of("/events/1", "/events/3"));
        assertEquals(1, statistic.size());
        assertEquals("/events/1", statistic.get(0).getUri());
        assertEquals(1, statistic.get(0).getHits());
    }

    @Test
    @Sql({"/schema_test.sql", "/import_table.sql"})
    void getStatisticNotUnique() {
        List<StatisticDtoInterface> statistic = repository.getStatisticNotUnique(now, now.plusMonths(12), List.of("/events/1", "/events/3"));
        assertEquals(1, statistic.size());
        assertEquals("/events/1", statistic.get(0).getUri());
        assertEquals(2, statistic.get(0).getHits());
    }

    @Test
    @Sql({"/schema_test.sql", "/import_table.sql"})
    void getStatisticNotUniqueOrdered() {
        List<StatisticDtoInterface> statistic = repository.getStatisticNotUnique(now, now.plusMonths(12), List.of("/events/1", "/events/2"));
        assertEquals(2, statistic.size());
        assertEquals("/events/1", statistic.get(0).getUri());
        assertEquals(2, statistic.get(0).getHits());
        assertEquals("/events/2", statistic.get(1).getUri());
        assertEquals(1, statistic.get(1).getHits());
    }

    @Test
    @Sql({"/schema_test.sql"})
    void save_whenOk() {
        Hit hit = new Hit(null, "ewm-main-service", "/events/1", "192.163.0.1", now.plusDays(1));
        Hit actualHit = repository.saveAndFlush(hit);
        assertEquals(1, repository.findAll().size());
        assertEquals(1, actualHit.getId());
        assertEquals("ewm-main-service", actualHit.getApp());
        assertEquals("/events/1", actualHit.getUri());
        assertEquals("192.163.0.1", actualHit.getIp());
        assertEquals(now.plusDays(1), actualHit.getRequestTime());
    }
}