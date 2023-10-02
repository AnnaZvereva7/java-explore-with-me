package ru.practicum.explore.ewm.categories.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CategoryDtoNew {
    @NotBlank(message = "must be not blank")
    @Size(max = 50, message = "Max size 50")
    private String name;
}
