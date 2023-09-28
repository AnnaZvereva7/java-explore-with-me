package ru.practicum.explore.ewm.users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.explore.ewm.common.OffsetBasedPageRequest;
import ru.practicum.explore.ewm.exceptions.NotFoundException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository repository;

    private UserService service;
    User user = new User(1L, "name", "email");

    @BeforeEach
    void setup() {
        service = new UserService(repository);
    }

    @Test
    void getUsers_whenAll() {
        when(repository.findAll(any(OffsetBasedPageRequest.class))).thenReturn(List.of(user));
        List<User> users = service.getUsers(null, 0, 10);
        assertEquals(1, users.size());
        assertEquals(1L, users.get(0).getId());
        assertEquals("name", users.get(0).getName());
        assertEquals("email", users.get(0).getEmail());
    }

    @Test
    void getUsers_whenListOfIds() {
        List<Long> ids = List.of(1L, 2L);
        when(repository.findUserByIdIn(eq(ids), any(OffsetBasedPageRequest.class))).thenReturn(List.of(user));
        List<User> users = service.getUsers(ids, 0, 10);
        assertEquals(1, users.size());
        assertEquals(1L, users.get(0).getId());
        assertEquals("name", users.get(0).getName());
        assertEquals("email", users.get(0).getEmail());
    }

    @Test
    void addUser() {
        User newUser = new User(null, "name", "email");
        when(repository.saveAndFlush(newUser)).thenReturn(user);
        User actualUser = service.addUser(newUser);
        assertEquals(user, actualUser);
    }

    @Test
    void delete() {
        service.delete(1L);
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void findById_whenOk() {
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        User actualUser = service.findById(1L);
        assertEquals(user, actualUser);
    }

    @Test
    void findById_whenEmpty() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        Throwable thrown = catchThrowable(() -> {
            service.findById(1L);
        });
        assertThat(thrown).isInstanceOf(NotFoundException.class);
    }
}