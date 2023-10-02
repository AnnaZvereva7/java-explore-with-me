package ru.practicum.explore.stats;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.explore.stats.dto.HitDto;

@Service
@Slf4j
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
        Hit hit = new Hit(null, hitDto.getApp(), hitDto.getUri(), hitDto.getIp(), hitDto.getRequestTime());
        log.info("save new hit {}", hit);
        return hit;
    }
}
