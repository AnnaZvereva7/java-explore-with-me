package ru.practicum.explore.ewm.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class RequestDtoCount implements ru.practicum.explore.ewm.requests.dto.RequestDtoCount {
    private Long event;
    private Integer requests;
}
