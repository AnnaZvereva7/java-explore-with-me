package ru.practicum.explore.stats;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.explore.stats.dto.StatisticDto;
import ru.practicum.explore.stats.dto.StatisticDtoInterface;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatsServiceTest {
    @Mock
    private StatsRepository repository;

    private StatsService service;

    @BeforeEach
    void setUp() {
        service = new StatsService(repository);
    }

    @Test
    void addHit() {
        Hit hit = new Hit(1L, "app", "uri", "ip", LocalDateTime.parse("2023-05-05 00:00:00", CommonConstant.FORMATTER));
        when(repository.saveAndFlush(hit)).thenReturn(hit);
        Hit actualHit = service.addHit(hit);
        assertEquals(1L, actualHit.getId());
        assertEquals("app", actualHit.getApp());
        assertEquals("uri", actualHit.getUri());
        assertEquals("ip", actualHit.getIp());
    }

    @Test
    void getStatisticUnique() {
        StatisticDto statisticDto = new StatisticDto("app", "uri", 2);
        LocalDateTime start = LocalDateTime.parse("2023-09-19 11:00:00", CommonConstant.FORMATTER);
        LocalDateTime end = start.plusMonths(10);
        when(repository.getStatisticUnique(start, end, List.of("uris"))).thenReturn(List.of(statisticDto));
        List<StatisticDtoInterface> actualList = service.getStatistic(start, end, List.of("uris"), true);

        assertEquals(1, actualList.size());
        assertEquals("app", actualList.get(0).getApp());
        assertEquals("uri", actualList.get(0).getUri());
        assertEquals(2, actualList.get(0).getHits());
    }

    @Test
    void getStatisticNotUnique() {
        StatisticDto statisticDto = new StatisticDto("app", "uri", 2);
        LocalDateTime start = LocalDateTime.parse("2023-09-19 11:00:00", CommonConstant.FORMATTER);
        LocalDateTime end = start.plusMonths(10);
        when(repository.getStatisticNotUnique(start, end, List.of("uris"))).thenReturn(List.of(statisticDto));
        List<StatisticDtoInterface> actualList = service.getStatistic(start, end, List.of("uris"), false);

        assertEquals(1, actualList.size());
        assertEquals("app", actualList.get(0).getApp());
        assertEquals("uri", actualList.get(0).getUri());
        assertEquals(2, actualList.get(0).getHits());
    }
}