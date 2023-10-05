package ru.practicum.explore.ewm.events.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.explore.ewm.events.EventService;
import ru.practicum.explore.ewm.events.dto.EventMapper;
import ru.practicum.explore.ewm.events.dto.request.AdminCommentDto;
import ru.practicum.explore.ewm.events.dto.request.EventsDtoConfirmation;
import ru.practicum.explore.ewm.events.model.stateAction.StateActionAdmin;
import ru.practicum.explore.ewm.exceptions.ErrorHandler;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class EventControllerAdminTest {
    @Mock
    private EventMapper mapper;

    @Mock
    private EventService service;
    @InjectMocks
    private EventControllerAdmin controller;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(ErrorHandler.class).build();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void findWithConditions() {
    }

    @Test
    void update() {
    }

    @Test
    void getEventForModeration() {
    }

    @Test
    void moderateEvent_whenEmptyIds() throws Exception {
        EventsDtoConfirmation dto = new EventsDtoConfirmation(List.of(), StateActionAdmin.PUBLISH_EVENT);
        mvc.perform(patch("/admin/events/moderation")
                        .content(objectMapper.writeValueAsString(dto))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is("BAD_REQUEST")))
                .andExpect(jsonPath("$.reason", is("Incorrectly made request.")))
                .andExpect(jsonPath("$.message", is("must be not empty")))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void addAdminComment_whenWrongDto() throws Exception {
        AdminCommentDto dto = new AdminCommentDto("com");
        mvc.perform(post("/admin/events/{eventId}/comment", 1L)
                        .content(objectMapper.writeValueAsString(dto))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is("BAD_REQUEST")))
                .andExpect(jsonPath("$.reason", is("Incorrectly made request.")))
                .andExpect(jsonPath("$.message", is("Size must be min 10 max 7000")))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}