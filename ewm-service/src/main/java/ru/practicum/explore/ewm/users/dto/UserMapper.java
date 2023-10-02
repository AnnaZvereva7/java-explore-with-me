package ru.practicum.explore.ewm.users.dto;

import org.springframework.stereotype.Component;
import ru.practicum.explore.ewm.users.User;

@Component
public class UserMapper {
    public UserDto fromUserToDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public UserShortDto fromUserToDtoShort(User user) {
        return new UserShortDto(user.getId(), user.getName());
    }

    public User fromDtoNewToUser(UserNewDto userNewDto) {
        return new User(userNewDto.getName(), userNewDto.getEmail());
    }
}
