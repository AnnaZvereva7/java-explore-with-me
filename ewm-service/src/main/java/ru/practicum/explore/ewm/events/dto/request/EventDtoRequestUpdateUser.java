package ru.practicum.explore.ewm.events.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @TwoHoursBeforeEventValid
    private LocalDateTime eventDate;
    private StateActionUser stateAction;
}
