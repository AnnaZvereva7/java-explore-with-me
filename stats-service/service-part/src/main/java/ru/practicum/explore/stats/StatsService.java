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
            if (uris == null) {
                return statsRepository.getAllStatisticUnique(start, end);
            } else {
                return statsRepository.getStatisticUnique(start, end, uris);
            }
        } else {
            if (uris == null) {
                return statsRepository.getAllStatisticNotUnique(start, end);
            } else {
                return statsRepository.getStatisticNotUnique(start, end, uris);
            }
        }
    }
}
