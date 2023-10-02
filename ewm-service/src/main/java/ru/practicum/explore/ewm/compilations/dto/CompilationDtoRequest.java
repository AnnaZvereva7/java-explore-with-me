package ru.practicum.explore.ewm.compilations.dto;

import lombok.*;
import org.springframework.validation.annotation.Validated;
import ru.practicum.explore.ewm.common.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Validated
public class CompilationDtoRequest {
    private Boolean pinned;
    @NotBlank(groups = Marker.OnCreate.class)
    @Size(max = 50, min = 3, groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    private String title;
    private Set<Long> events;
}
