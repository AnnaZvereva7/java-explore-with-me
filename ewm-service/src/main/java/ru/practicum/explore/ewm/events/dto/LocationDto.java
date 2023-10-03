package ru.practicum.explore.ewm.events.dto;

import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LocationDto {
    @Max(value = 90)
    @Min(value = -90)
    @NotNull
    private Double lat;
    @Max(value = 180)
    @Min(value = -180)
    @NotNull
    private Double lon;
}
