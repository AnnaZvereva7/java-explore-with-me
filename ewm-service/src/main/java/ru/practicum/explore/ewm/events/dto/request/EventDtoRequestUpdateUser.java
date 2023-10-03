package ru.practicum.explore.ewm.events.dto.request;

import lombok.*;
import ru.practicum.explore.ewm.events.model.stateAction.StateActionUser;
import ru.practicum.explore.ewm.exceptions.eventDateValidator.TwoHoursBeforeEventValid;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EventDtoRequestUpdateUser extends EventDtoRequestUpdate {
    @TwoHoursBeforeEventValid
    private LocalDateTime eventDate;
    private StateActionUser stateAction;
}
