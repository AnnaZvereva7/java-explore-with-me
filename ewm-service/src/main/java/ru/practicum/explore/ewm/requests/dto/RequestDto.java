package ru.practicum.explore.ewm.requests.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.practicum.explore.ewm.requests.model.Status;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class RequestDto {
    private Long id;
    @JsonProperty(value = "event")
    private Long eventId;
    @JsonProperty(value = "requester")
    private Long requesterId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;
    private Status status;
}
