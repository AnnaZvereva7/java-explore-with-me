package ru.practicum.explore.ewm.users;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.explore.ewm.common.OffsetBasedPageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository repository;

    Sort sort = Sort.by("id").ascending();

    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void findUserByIdIn_whenOk() {
        List<User> actualUsers = repository.findUserByIdIn(List.of(1L, 2L), new OffsetBasedPageRequest(0, 10, sort));
        assertEquals(2, actualUsers.size());
        assertEquals("name1", actualUsers.get(0).getName());
        assertEquals("email2@mail.ru", actualUsers.get(1).getEmail());
    }

    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void findUserByIdIn_whenWrongIds() {
        List<User> actualUsers = repository.findUserByIdIn(List.of(5L), new OffsetBasedPageRequest(0, 10, sort));
        assertEquals(0, actualUsers.size());
    }

    @Test
    @Sql({"/schemaTest.sql"})
    void findUserByIdIn_whenEmpty() {
        List<User> actualUsers = repository.findUserByIdIn(List.of(1L), new OffsetBasedPageRequest(0, 10, sort));
        assertEquals(0, actualUsers.size());
    }

    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void findById_whenOk() {
        Optional<User> userOptional = repository.findById(1L);
        assertThat(userOptional).isPresent();
        assertEquals(1L, userOptional.get().getId());
        assertEquals("name1", userOptional.get().getName());
        assertEquals("email1@mail.ru", userOptional.get().getEmail());
    }

    @Test
    @Sql({"/schemaTest.sql"})
    void findById_whenEmpty() {
        Optional<User> userOptional = repository.findById(5L);
        assertThat(userOptional).isEmpty();
    }

    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void findAll_whenOk() {
        List<User> actualUsers = repository.findAll(new OffsetBasedPageRequest(0, 10, sort));
        assertEquals(4, actualUsers.size());
        assertEquals(1L, actualUsers.get(0).getId());
        assertEquals(4L, actualUsers.get(3).getId());
    }

    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void findAll_whenFrom() {
        List<User> actualUsers = repository.findAll(new OffsetBasedPageRequest(2, 10, sort));
        assertEquals(2, actualUsers.size());
        assertEquals(3L, actualUsers.get(0).getId());
        assertEquals(4L, actualUsers.get(1).getId());
    }

    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void findAll_whenSize() {
        List<User> actualUsers = repository.findAll(new OffsetBasedPageRequest(0, 2, sort));
        assertEquals(2, actualUsers.size());
        assertEquals(1L, actualUsers.get(0).getId());
        assertEquals(2L, actualUsers.get(1).getId());

    }

    @Test
    @Sql({"/schemaTest.sql"})
    void save_whenOk() {
        User newUser = new User(null, "name", "email@mail.ru");
        User user = repository.saveAndFlush(newUser);
        assertEquals(1L, user.getId());
        assertEquals(1, repository.findAll().size());
    }

    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void save_whenEmailNotUnique() {
        User newUser = new User(null, "name", "email1@mail.ru");
        Throwable thrown = catchThrowable(() -> {
            repository.saveAndFlush(newUser);
        });
        assertThat(thrown).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void delete_whenOk() {
        repository.deleteById(1L);
        assertEquals(3, repository.findAll().size());
        assertThat(repository.findById(1L)).isEmpty();
    }

    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void delete_whenNotExist() {
        Throwable thrown = catchThrowable(() -> {
            repository.deleteById(5L);
        });
        assertThat(thrown).isInstanceOf(EmptyResultDataAccessException.class);
        assertEquals(4, repository.findAll().size());
    }
}