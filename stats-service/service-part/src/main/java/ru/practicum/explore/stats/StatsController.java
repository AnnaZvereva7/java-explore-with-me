package ru.practicum.explore.stats;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.stats.dto.HitDto;
import ru.practicum.explore.stats.dto.StatisticDto;
import ru.practicum.explore.stats.exception.WrongPeriodException;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class StatsController {
    private final StatsService statsService;

    @PostMapping("/hit")
    public ResponseEntity<Void> addHit(@RequestBody @Valid HitDto hit) {
        log.info("Get hit app={}, uri={}, ip={}, timeshtamp={}", hit.getApp(), hit.getUri(), hit.getIp(), hit.getRequestTime());
        statsService.addHit(new Hit(hit));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/stats")
    public List<StatisticDto> getStatistic(@RequestParam(name = "start") String start,
                                           @RequestParam(name = "end") String end,
                                           @RequestParam(name = "uris") String[] uris,
                                           @RequestParam(defaultValue = "false", name = "unique") boolean unique) {
        LocalDateTime startPeriod = LocalDateTime.parse(start, CommonConstant.FORMATTER);
        LocalDateTime endPeriod = LocalDateTime.parse(end, CommonConstant.FORMATTER);
        if (startPeriod.isAfter(endPeriod) || startPeriod.isEqual(endPeriod)) {
            throw new WrongPeriodException();
        }
        log.info("Get statistic period from {} to {}, uris: {}, unique= {}", start, end, uris, unique);
        List<String> urisList = new ArrayList<>();
        for (int i = 0; i < uris.length; i++) {
            if (!uris[i].isBlank()) {
                urisList.add(uris[i]);
            }
        }
        if (urisList.size() == 0) {
            return List.of();
        }

        return statsService.getStatistic(startPeriod, endPeriod, urisList, unique)
                .stream()
                .map(k -> new StatisticDto(k))
                .collect(Collectors.toList());
    }
}
