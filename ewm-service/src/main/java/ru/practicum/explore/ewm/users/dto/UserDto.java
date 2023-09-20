package ru.practicum.explore.ewm.users.dto;

import ru.practicum.explore.ewm.users.User;

public class UserDto {
    private Long id;
    private String name;
    private String email;

    public UserDto (User user) {
        this.id= user.getId();
        this.name= user.getName();
        this.email= user.getEmail();
    }
}
