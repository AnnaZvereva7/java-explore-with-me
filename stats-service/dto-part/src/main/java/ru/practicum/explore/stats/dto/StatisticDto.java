package ru.practicum.explore.stats.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class StatisticDto implements StatisticDtoInterface {
    private String app;
    private String uri;
    private int hits;

    public StatisticDto(StatisticDtoInterface statisticDtoInterface) {
        this.app = statisticDtoInterface.getApp();
        this.uri = statisticDtoInterface.getUri();
        this.hits = statisticDtoInterface.getHits();
    }
}
