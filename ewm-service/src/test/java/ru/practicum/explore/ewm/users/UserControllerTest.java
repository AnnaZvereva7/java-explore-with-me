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
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


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
    UserNewDto newUser = new UserNewDto("name", "email@mail.ru");

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(ErrorHandler.class).build();
    }

    @Test
    void getUsers_whenOk() throws Exception {
        List<User> users= new ArrayList<>(List.of(user));
        when(service.getUsers(anyList(), eq(1), eq(5))).thenReturn(users);
        when(mapper.fromUserToDto(user)).thenReturn(userDto);
        mvc.perform(get("/admin/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .param("ids", "1")
                        .param("from", "1")
                        .param("size", "5"))
                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].id", is(1), Long.class));
    }

    @Test
    void addUser() {
    }

    @Test
    void delete() {
    }
}