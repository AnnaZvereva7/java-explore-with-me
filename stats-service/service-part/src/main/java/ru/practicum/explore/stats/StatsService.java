package ru.practicum.explore.stats;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.explore.stats.dto.StatisticDtoInterface;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsService {
    private final StatsRepository statsRepository;

    public Hit addHit(Hit hit) {
        log.info("save new hit {}", hit);
        Hit savedHit = statsRepository.saveAndFlush(hit);
        log.info("saved hit {}", savedHit);
        return savedHit;
    }

    public List<StatisticDtoInterface> getStatistic(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {

        if (unique) {
            return statsRepository.getStatisticUnique(start, end, uris);
        } else {
            return statsRepository.getStatisticNotUnique(start, end, uris);
        }
    }
}
