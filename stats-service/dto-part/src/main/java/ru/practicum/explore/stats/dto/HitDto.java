package ru.practicum.explore.stats.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.explore.stats.dto.validator.RequestTimeValid;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HitDto {
    private Long id;
    @NotBlank(message = "app must be not blank")
    private String app;
    private String uri;
    @Pattern(message = "ip does not match the pattern", regexp = "((25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9][0-9]|[0-9])(\\.|$)){4}")
    private String ip;
    @JsonProperty(value = "timestamp")
    @NotBlank(message = "time must be not blank")
    @RequestTimeValid(message = "time is incorrect")
    private String requestTime;
}
