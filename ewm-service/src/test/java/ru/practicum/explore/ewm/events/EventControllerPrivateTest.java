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
import ru.practicum.explore.ewm.common.CommonConstant;
import ru.practicum.explore.ewm.events.controller.EventControllerPrivate;
import ru.practicum.explore.ewm.events.dto.EventDtoRequest;
import ru.practicum.explore.ewm.events.dto.EventMapper;
import ru.practicum.explore.ewm.events.dto.LocationDto;
import ru.practicum.explore.ewm.exceptions.ErrorHandler;
import ru.practicum.explore.ewm.exceptions.EventDateException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doThrow;
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
        EventDtoRequest dto = new EventDtoRequest("    ", "annotationannotationannotation", "descriptiondescriptiondescription", 1, now.plusDays(1), new LocationDto(56.15f, 45.12f), true, 10, true, null);
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
        EventDtoRequest dto = new EventDtoRequest("title", "annotationannotationannotation", "descriptiondescriptiondescription", 1, now.plusDays(1), new LocationDto(200f, 45.12f), true, 10, true, null);
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
        EventDtoRequest dto = new EventDtoRequest("title", "annotationannotationannotation", "descriptiondescriptiondescriptiondescription", 1, now.plusHours(1), new LocationDto(56.15f, 45.12f), true, 10, true, null);
        doThrow(new EventDateException(now.plusHours(1))).when(service).checkEvenDate(now.plusHours(1), 2);
        mvc.perform(post("/users/{userId}/events", 1L)
                        .content(objectMapper.writeValueAsString(dto))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is("BAD_REQUEST")))
                .andExpect(jsonPath("$.reason", is("Incorrectly made request.")))
                .andExpect(jsonPath("$.message", is("Field: eventDate. Error: должно содержать дату, которая еще не наступила. Value: " + now.plusHours(1).format(CommonConstant.FORMATTER))))
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