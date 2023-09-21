package ru.practicum.explore.stats;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explore.stats.dto.StatisticDtoInterface;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<Hit, Long> {

    @Query(value = "select h.app as app, h.uri as uri, count(distinct h.ip) as hits from Hit h " +
            "where (h.requestTime BETWEEN :startPeriod and :endPeriod ) and " +
            "((:uris) is null OR h.uri in (:uris)) " +
            "group by h.app, h.uri order by count(distinct h.ip) desc")
    List<StatisticDtoInterface> getStatisticUnique(@Param("startPeriod") LocalDateTime start,
                                                   @Param("endPeriod") LocalDateTime end,
                                                   @Param("uris") List<String> uris);

    @Query(value = "select h.app as app, h.uri as uri, count(h.id) as hits from Hit h " +
            "where (h.requestTime BETWEEN :startPeriod and :endPeriod) and " +
            "((:uris) is null OR h.uri in (:uris)) " +
            "group by h.app, h.uri order by count(h.id) desc")
    List<StatisticDtoInterface> getStatisticNotUnique(@Param("startPeriod") LocalDateTime start,
                                                      @Param("endPeriod") LocalDateTime end,
                                                      @Param("uris") List<String> uris);
}
