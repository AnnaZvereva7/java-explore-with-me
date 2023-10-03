package ru.practicum.explore.ewm.events;

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
import ru.practicum.explore.ewm.events.controller.EventControllerPrivate;
import ru.practicum.explore.ewm.events.dto.EventMapper;
import ru.practicum.explore.ewm.events.dto.LocationDto;
import ru.practicum.explore.ewm.events.dto.request.EventDtoRequestCreate;
import ru.practicum.explore.ewm.exceptions.ErrorHandler;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class EventControllerPrivateTest {

    @Mock
    private EventMapper mapper;

    @Mock
    private EventService service;
    @InjectMocks
    private EventControllerPrivate controller;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mvc;
    LocalDateTime now = LocalDateTime.now().withNano(0);

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(ErrorHandler.class).build();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void getEventsByUser() {
    }

    @Test
    void addEvent_whenWrongDto() throws Exception {
        EventDtoRequestCreate dto = new EventDtoRequestCreate("    ", "annotationannotationannotation", "descriptiondescriptiondescription",
                1, now.plusDays(1), new LocationDto(new BigDecimal(56.12), new BigDecimal(45.12)), true, 10, true);
        mvc.perform(post("/users/{userId}/events", 1L)
                        .content(objectMapper.writeValueAsString(dto))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is("BAD_REQUEST")))
                .andExpect(jsonPath("$.reason", is("Incorrectly made request.")))
                .andExpect(jsonPath("$.message", is("Title must be not blank")))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void addEvent_whenWrongLocation() throws Exception {
        EventDtoRequestCreate dto = new EventDtoRequestCreate("title", "annotationannotationannotation", "descriptiondescriptiondescription",
                1, now.plusDays(1), new LocationDto(new BigDecimal(200), new BigDecimal(45.12)), true, 10, true);
        mvc.perform(post("/users/{userId}/events", 1L)
                        .content(objectMapper.writeValueAsString(dto))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is("BAD_REQUEST")))
                .andExpect(jsonPath("$.reason", is("Incorrectly made request.")))
                .andExpect(jsonPath("$.message", is("must be less than or equal to 90")))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void addEvent_when1HourBeforeEvent() throws Exception {
        EventDtoRequestCreate dto = new EventDtoRequestCreate("title", "annotationannotationannotation", "descriptiondescriptiondescriptiondescription",
                1, now.plusHours(1), new LocationDto(new BigDecimal(56.15), new BigDecimal(45.12)), true, 10, true);
        mvc.perform(post("/users/{userId}/events", 1L)
                        .content(objectMapper.writeValueAsString(dto))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is("BAD_REQUEST")))
                .andExpect(jsonPath("$.reason", is("Incorrectly made request.")))
                .andExpect(jsonPath("$.message", is("Must be more then 2 hour before event start")))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void getEventById() {
    }

    @Test
    void updateEvent() {
    }

    @Test
    void getRequestsByEventId() {
    }

    @Test
    void moderateRequests() {
    }
}