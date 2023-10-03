package ru.practicum.explore.ewm.events.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.explore.ewm.events.model.stateAction.StateActionAdmin;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventsDtoConfirmation {
    @NotNull(message = "must be not null")
    @NotEmpty(message = "must be not empty")
    private List<Long> ids;
    @NotNull(message = "must be not null")
    private StateActionAdmin stateAction;

}
