package ru.practicum.explore.ewm.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.practicum.explore.ewm.common.Marker;
import ru.practicum.explore.ewm.events.model.StateAction;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class EventDtoRequest {
    @NotBlank(groups = Marker.OnCreate.class, message = "Title must be not blank")
    @Size(min = 3, max = 120, groups = {Marker.OnUpdate.class, Marker.OnCreate.class}, message = "Size of Title max=120, min=3")
    private String title;
    @NotBlank(groups = Marker.OnCreate.class, message = "Annotation must be not blank")
    @Size(min = 20, max = 2000, groups = {Marker.OnUpdate.class, Marker.OnCreate.class}, message = "Size of Annotation max=2000, min=20")
    private String annotation;
    @NotBlank(groups = Marker.OnCreate.class, message = "Description must be not blank")
    @Size(min = 20, max = 7000, groups = {Marker.OnUpdate.class, Marker.OnCreate.class}, message = "Size of Description max=7000, min=20")
    private String description;
    @NotNull(groups = Marker.OnCreate.class, message = "Category must be not null")
    @JsonProperty(value = "category")
    private Integer categoryId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @JsonProperty(value = "location")
    @Valid
    @NotNull
    private LocationDto locationDto;
    private Boolean paid;
    @PositiveOrZero(groups = {Marker.OnUpdate.class, Marker.OnCreate.class}, message = "Participant limit must be >=0")
    private Integer participantLimit;
    private Boolean requestModeration;
    private StateAction stateAction;
}

