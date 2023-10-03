package ru.practicum.explore.ewm.events.dto.request;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class AdminCommentDto {
    @Size(min = 10, max = 7000, message = "Size must be min 10 max 7000")
    @NotNull(message = "must be not null")
    private String comment;
}
