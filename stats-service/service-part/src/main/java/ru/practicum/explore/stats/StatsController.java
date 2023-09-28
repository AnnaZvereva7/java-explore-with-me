package ru.practicum.explore.stats;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.stats.dto.HitDto;
import ru.practicum.explore.stats.dto.StatisticDto;
import ru.practicum.explore.stats.dto.StatsMapper;
import ru.practicum.explore.stats.exception.WrongPeriodException;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class StatsController {
    private final StatsService statsService;
    private final HitMapper mapper;
    private final StatsMapper statsMapper;

    @PostMapping("/hit")
    public HitDto addHit(@RequestBody @Valid HitDto hit) {
        log.info("Get hit app={}, uri={}, ip={}, timeshtamp={}", hit.getApp(), hit.getUri(), hit.getIp(), hit.getRequestTime());
        return mapper.toHitDto(statsService.addHit(mapper.fromHitDto(hit)));
    }

    @GetMapping("/stats")
    public List<StatisticDto> getStatistic(@RequestParam(name = "start") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                           @RequestParam(name = "end") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                           @RequestParam(name = "uris", required = false) List<String> uris,
                                           @RequestParam(defaultValue = "false", name = "unique") boolean unique) {
        if (start.isAfter(end) || start.isEqual(end)) {
            throw new WrongPeriodException();
        }
        log.info("Get statistic period from {} to {}, uris: {}, unique= {}", start, end, uris, unique);

        if (uris != null) {
            uris.removeIf(String::isBlank);
            if (uris.isEmpty()) {
                return List.of();
            }
        }
        List<StatisticDto> result = statsService.getStatistic(start, end, uris, unique)
                .stream()
                .map(statsMapper::fromInterface)
                .collect(Collectors.toList());
        log.info("return list of statistics {}", result);
        return result;
    }
}
