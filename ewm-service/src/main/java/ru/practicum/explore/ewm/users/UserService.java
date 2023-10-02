package ru.practicum.explore.ewm.users;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.explore.ewm.common.OffsetBasedPageRequest;
import ru.practicum.explore.ewm.exceptions.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final Sort sort = Sort.by("id").ascending();

    public List<User> getUsers(List<Long> ids, int from, int size) {
        if (ids == null) {
            return repository.findAll(new OffsetBasedPageRequest(from, size, sort));
        } else {
            return repository.findUserByIdIn(ids, new OffsetBasedPageRequest(from, size, sort));
        }
    }

    public User addUser(User user) {
        return repository.saveAndFlush(user);
    }

    public void delete(Long userId) {
        repository.deleteById(userId);
    }

    public User findById(Long userId) {
        return repository.findById(userId).orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
    }

}
