package ru.practicum.explore.ewm.events.dto.request;

import lombok.*;
import ru.practicum.explore.ewm.events.model.stateAction.StateActionAdmin;
import ru.practicum.explore.ewm.exceptions.eventDateValidator.HourBeforeEventValid;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Builder
public class EventDtoRequestUpdateAdmin extends EventDtoRequestUpdate {
    @HourBeforeEventValid
    private LocalDateTime eventDate;
    private StateActionAdmin stateAction;
    private String adminComment;
}
