package ru.practicum.explore.ewm;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.explore.ewm.categories.CategoryService;
import ru.practicum.explore.ewm.categories.dto.CategoryMapper;
import ru.practicum.explore.ewm.compilations.CompilationService;
import ru.practicum.explore.ewm.compilations.dto.CompilationMapper;
import ru.practicum.explore.ewm.events.EventService;
import ru.practicum.explore.ewm.events.dto.EventMapper;
import ru.practicum.explore.ewm.requests.RequestService;
import ru.practicum.explore.ewm.requests.dto.RequestMapper;
import ru.practicum.explore.ewm.users.UserService;
import ru.practicum.explore.ewm.users.dto.UserMapper;
import ru.practicum.explore.stats.client.StatsClient;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@AutoConfigureWebClient
public class ControllersParamTest {
    @Autowired
    private WebApplicationContext wac;

    @MockBean
    private CategoryMapper categoryMapper;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private CompilationService compilationService;

    @MockBean
    private CompilationMapper compilationMapper;

    @MockBean
    private EventService eventService;

    @MockBean
    private EventMapper eventMapper;

    @MockBean
    private UserService userService;
    @MockBean
    private UserMapper userMapper;

    @MockBean
    private RequestMapper requestMapper;

    @MockBean
    private RequestService requestService;
    @MockBean
    private StatsClient client;

    private MockMvc mvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    void userControllerAdmin_getUsers_whenWrongFrom() throws Exception {
        mvc.perform(get("/admin/users")
                        .param("from", "-4")
                        .param("size", "20")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is("BAD_REQUEST")))
                .andExpect(jsonPath("$.reason", is("Incorrectly made request.")))
                .andExpect(jsonPath("$.message", is("getUsers.from: must be greater than or equal to 0")))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void requestControllerPrivate_post() throws Exception {
        mvc.perform(post("/users/{userId}/requests", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is("BAD_REQUEST")));

    }

    @Test
    void categoryControllerPublic_getCategories_whenWrongFrom() throws Exception {
        mvc.perform(get("/categories")
                        .param("from", "-1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is("BAD_REQUEST")))
                .andExpect(jsonPath("$.reason", is("Incorrectly made request.")))
                .andExpect(jsonPath("$.message", is("getCategories.from: must be greater than or equal to 0")))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void categoryControllerPublic_getCategories_whenWrongSize() throws Exception {
        mvc.perform(get("/categories")
                        .param("size", "0")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is("BAD_REQUEST")))
                .andExpect(jsonPath("$.reason", is("Incorrectly made request.")))
                .andExpect(jsonPath("$.message", is("getCategories.size: must be greater than 0")))
                .andExpect(jsonPath("$.timestamp").exists());
    }

}
