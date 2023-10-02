package ru.practicum.explore.ewm.requests.dto;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RequestDtoConfirmation {
    private Set<Long> requestIds;
    private StatusConfirmation status;

    public enum StatusConfirmation {
        CONFIRMED,
        REJECTED
    }
}
