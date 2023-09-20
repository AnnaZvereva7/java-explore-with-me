package ru.practicum.explore.stats.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class HitDto {
    private Long id;
    @NotBlank(message = "app must be not blank")
    private String app;
    private String uri;
    @Pattern(message = "ip does not match the pattern", regexp = "((25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9][0-9]|[0-9])(\\.|$)){4}")
    private String ip;
    @JsonProperty(value = "timestamp")
    @NotNull(message = "time must be not null")
    @Past(message = "time must be in the past")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime requestTime;
}
