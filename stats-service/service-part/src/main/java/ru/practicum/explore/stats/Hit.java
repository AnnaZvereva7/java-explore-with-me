package ru.practicum.explore.stats;

import lombok.*;
import ru.practicum.explore.stats.dto.HitDto;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Entity
@Table(name = "stat_data")
public class Hit {
    @EqualsAndHashCode.Exclude
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String app;
    private String uri;
    private String ip;
    @Column(name = "request_time")
    private LocalDateTime requestTime;

    public Hit(HitDto hitDto) {
        this.app = hitDto.getApp();
        this.uri = hitDto.getUri();
        this.ip = hitDto.getIp();
        this.requestTime = LocalDateTime.parse(hitDto.getRequestTime(), CommonConstant.FORMATTER);
    }

    public HitDto toHitDto() {
        HitDto hitDto = new HitDto();
        hitDto.setId(this.getId());
        hitDto.setApp(this.getApp());
        hitDto.setUri(this.getUri());
        hitDto.setIp(this.getIp());
        hitDto.setRequestTime(this.getRequestTime().format(CommonConstant.FORMATTER));
        return hitDto;
    }
}
