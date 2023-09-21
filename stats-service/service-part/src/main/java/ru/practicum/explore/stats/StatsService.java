package ru.practicum.explore.stats;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.explore.stats.dto.StatisticDtoInterface;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsService {
    private final StatsRepository statsRepository;

    public Hit addHit(Hit hit) {
        return statsRepository.saveAndFlush(hit);
    }

    public List<StatisticDtoInterface> getStatistic(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {

        if (unique) {
            return statsRepository.getStatisticUnique(start, end, uris);
        } else {
            return statsRepository.getStatisticNotUnique(start, end, uris);
        }
    }
}
