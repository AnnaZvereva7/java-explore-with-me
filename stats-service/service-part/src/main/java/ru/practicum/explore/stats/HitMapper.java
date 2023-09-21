package ru.practicum.explore.stats;

import org.springframework.stereotype.Service;
import ru.practicum.explore.stats.dto.HitDto;

@Service
public class HitMapper {

    public HitDto toHitDto(Hit hit) {
        HitDto hitDto = new HitDto();
        hitDto.setId(hit.getId());
        hitDto.setApp(hit.getApp());
        hitDto.setUri(hit.getUri());
        hitDto.setIp(hit.getIp());
        hitDto.setRequestTime(hit.getRequestTime());
        return hitDto;
    }

    public Hit fromHitDto(HitDto hitDto) {
        return new Hit(null, hitDto.getApp(), hitDto.getUri(), hitDto.getIp(), hitDto.getRequestTime());
    }
}
