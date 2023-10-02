package ru.practicum.explore.ewm.events.dto;

import lombok.*;
import ru.practicum.explore.ewm.common.Marker;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LocationDto {
    @Max(value = 90, groups = {Marker.OnUpdate.class, Marker.OnCreate.class})
    @Min(value = -90, groups = {Marker.OnUpdate.class, Marker.OnCreate.class})
    @NotNull(groups = Marker.OnCreate.class)
    private Float lat;
    @Max(value = 180, groups = {Marker.OnUpdate.class, Marker.OnCreate.class})
    @Min(value = -180, groups = {Marker.OnUpdate.class, Marker.OnCreate.class})
    @NotNull(groups = Marker.OnCreate.class)
    private Float lon;
}
