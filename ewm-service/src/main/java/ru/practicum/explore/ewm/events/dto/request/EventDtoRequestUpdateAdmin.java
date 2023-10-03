package ru.practicum.explore.ewm.events.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @HourBeforeEventValid
    private LocalDateTime eventDate;
    private StateActionAdmin stateAction;
    private String adminComment;
}
