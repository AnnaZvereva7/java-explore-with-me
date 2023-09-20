package ru.practicum.explore.ewm.users.dto;

import lombok.AllArgsConstructor;
import ru.practicum.explore.ewm.users.User;

@AllArgsConstructor
public class UserShortDto {
    private Long id;
    private String name;

    public UserShortDto(User user) {
        this.id = user.getId();
        this.name= user.getName();
    }
}
