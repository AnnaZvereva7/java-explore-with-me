package ru.practicum.explore.ewm.users.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.explore.ewm.users.User;
@Getter
@Setter
@AllArgsConstructor
public class UserShortDto {
    private Long id;
    private String name;

}
