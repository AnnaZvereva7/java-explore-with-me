package ru.practicum.explore.stats;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explore.stats.dto.StatisticDtoInterface;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<Hit, Long> {

    @Query(value = "select app as app, uri as uri, count(distinct ip) as hits from stat_data " +
            "where request_time>:startPeriod and request_time<:endPeriod and uri in :uris " +
            "group by app, uri order by hits desc", nativeQuery = true)
    List<StatisticDtoInterface> getStatisticUnique(@Param("startPeriod") LocalDateTime start,
                                                   @Param("endPeriod") LocalDateTime end,
                                                   @Param("uris") List<String> uris);

    @Query(value = "select app as app, uri as uri, count(id) as hits from stat_data " +
            "where request_time>:startPeriod and request_time<:endPeriod and uri in :uris " +
            "group by app, uri order by hits desc", nativeQuery = true)
    List<StatisticDtoInterface> getStatisticNotUnique(@Param("startPeriod") LocalDateTime start,
                                                      @Param("endPeriod") LocalDateTime end,
                                                      @Param("uris") List<String> uris);

    @Query(value = "select app as app, uri as uri, count(distinct ip) as hits from stat_data " +
            "where request_time>:startPeriod and request_time<:endPeriod " +
            "group by app, uri order by hits desc", nativeQuery = true)
    List<StatisticDtoInterface> getAllStatisticUnique(@Param("startPeriod") LocalDateTime start,
                                                      @Param("endPeriod") LocalDateTime end);

    @Query(value = "select app as app, uri as uri, count(id) as hits from stat_data " +
            "where request_time>:startPeriod and request_time<:endPeriod " +
            "group by app, uri order by hits desc", nativeQuery = true)
    List<StatisticDtoInterface> getAllStatisticNotUnique(@Param("startPeriod") LocalDateTime start,
                                                         @Param("endPeriod") LocalDateTime end);
}
