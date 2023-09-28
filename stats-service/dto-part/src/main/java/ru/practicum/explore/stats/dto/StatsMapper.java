package ru.practicum.explore.stats.dto;

import org.springframework.stereotype.Component;

@Component
public class StatsMapper {

    public StatisticDto fromInterface(StatisticDtoInterface interfaceDto) {
        return new StatisticDto(interfaceDto.getApp(), interfaceDto.getUri(), interfaceDto.getHits());
    }

    public Long getEventId(StatisticDto dto) {
        String uri = dto.getUri();
        return Long.parseLong(uri.substring(uri.lastIndexOf("/") + 1, uri.length()));
    }
}
