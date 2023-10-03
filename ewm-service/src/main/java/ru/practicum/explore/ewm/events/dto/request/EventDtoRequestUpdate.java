package ru.practicum.explore.ewm.events.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.practicum.explore.ewm.events.dto.LocationDto;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class EventDtoRequestUpdate {
    @Size(min = 3, max = 120, message = "Size of Title max=120, min=3")
    private String title;
    @Size(min = 20, max = 2000, message = "Size of Annotation max=2000, min=20")
    private String annotation;
    @Size(min = 20, max = 7000, message = "Size of Description max=7000, min=20")
    private String description;
    @JsonProperty(value = "category")
    private Integer categoryId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty(value = "location")
    @Valid
    private LocationDto locationDto;
    private Boolean paid;
    @PositiveOrZero(message = "Participant limit must be >=0")
    private Integer participantLimit;
    private Boolean requestModeration;
}
