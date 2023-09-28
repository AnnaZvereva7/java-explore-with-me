package ru.practicum.explore.ewm.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.explore.ewm.exceptions.ErrorHandler;
import ru.practicum.explore.ewm.users.dto.UserDto;
import ru.practicum.explore.ewm.users.dto.UserMapper;
import ru.practicum.explore.ewm.users.dto.UserNewDto;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private UserMapper mapper;

    @Mock
    private UserService service;
    @InjectMocks
    private UserController controller;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mvc;
    User user = new User(1L, "name", "email@mail.ru");
    UserDto userDto = new UserDto(1L, "name", "email@mail.ru");
    UserNewDto newUserDto = new UserNewDto("name", "email@mail.ru");

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(ErrorHandler.class).build();
    }

    @Test
    void getUsers_whenOk() throws Exception {
        List<User> users = new ArrayList<>(List.of(user));
        when(service.getUsers(List.of(1L, 2L), 1, 5)).thenReturn(users);
        when(mapper.fromUserToDto(user)).thenReturn(userDto);
        mvc.perform(get("/admin/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .param("ids", "1")
                        .param("ids", "2")
                        .param("from", "1")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].id", is(1L), Long.class))
                .andExpect(jsonPath("$[0].name", is("name")));
    }

    @Test
    void getUsers_whenNoIds() throws Exception {
        List<User> users = new ArrayList<>(List.of(user));
        when(service.getUsers(null, 1, 5)).thenReturn(users);
        when(mapper.fromUserToDto(user)).thenReturn(userDto);
        mvc.perform(get("/admin/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .param("from", "1")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].id", is(1L), Long.class))
                .andExpect(jsonPath("$[0].name", is("name")));
    }

    @Test
    void getUsers_whenNoParam() throws Exception {
        List<User> users = new ArrayList<>(List.of(user));
        when(service.getUsers(null, 0, 10)).thenReturn(users);
        when(mapper.fromUserToDto(user)).thenReturn(userDto);
        mvc.perform(get("/admin/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].id", is(1L), Long.class))
                .andExpect(jsonPath("$[0].name", is("name")));
    }

    @Test
    void addUser() throws Exception {
        User newUser = new User(1L, "name", "email@mail.ru");
        when(mapper.fromDtoNewToUser(newUserDto)).thenReturn(newUser);
        when(service.addUser(newUser)).thenReturn(user);
        when(mapper.fromUserToDto(user)).thenReturn(userDto);
        mvc.perform(post("/admin/users")
                        .content(objectMapper.writeValueAsString(newUserDto))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.name", is("name")))
                .andExpect(jsonPath("$.email", is("email@mail.ru")));
    }

    @Test
    void addUser_whenWrongName() throws Exception {
        UserNewDto newUser1 = new UserNewDto("   ", "email@mail.ru");

        mvc.perform(post("/admin/users")
                        .content(objectMapper.writeValueAsString(newUser1))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is("BAD_REQUEST")))
                .andExpect(jsonPath("$.reason", is("Incorrectly made request.")))
                .andExpect(jsonPath("$.message", is("Name must be not blank")))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void addUser_whenWrongEmail() throws Exception {
        UserNewDto newUser1 = new UserNewDto("name", "email mail.ru");

        mvc.perform(post("/admin/users")
                        .content(objectMapper.writeValueAsString(newUser1))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is("BAD_REQUEST")))
                .andExpect(jsonPath("$.reason", is("Incorrectly made request.")))
                .andExpect(jsonPath("$.message", is("Wrong email pattern")))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void delete_whenOk() throws Exception {
        doNothing().when(service).delete(1L);
        mvc.perform(delete("/admin/users/{userId}", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk());
        verify(service, times(1)).delete(1L);
    }
}