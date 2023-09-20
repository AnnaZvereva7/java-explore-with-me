package ru.practicum.explore.ewm.users;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;

    public List<User> getUsers (List<Long> ids) {
        return  null;
    }
}
