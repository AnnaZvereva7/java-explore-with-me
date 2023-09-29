package ru.practicum.explore.stats;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.explore.stats.dto.HitDto;
import ru.practicum.explore.stats.dto.StatisticDto;
import ru.practicum.explore.stats.dto.StatsMapper;
import ru.practicum.explore.stats.exception.ErrorHandler;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class StatsControllerTest {
    @Mock
    private StatsService service;
    @Mock
    private HitMapper mapper;
    @Mock
    private StatsMapper statsMapper;
    @InjectMocks
    private StatsController controller;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(ErrorHandler.class).build();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void addHit_whenOk() throws Exception {
        HitDto hitDto = new HitDto(null, "ewm-main-service", "/events/1", "192.163.0.1", LocalDateTime.parse("2022-09-06 11:00:23", CommonConstant.FORMATTER));
        Hit hit = new Hit(1L, "ewm-main-service", "/events/1", "192.163.0.1", LocalDateTime.parse("2022-09-06 11:00:23", CommonConstant.FORMATTER));
        when(mapper.fromHitDto(hitDto)).thenReturn(hit);
        when(service.addHit(hit)).thenReturn(hit);
        mvc.perform(post("/hit")
                        .content(objectMapper.writeValueAsString(hitDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isOk());
    }

    @Test
    void addHit_whenWrongApp() throws Exception {
        HitDto hitDto = new HitDto(null, "  ", "/events/1", "192.163.0.1", LocalDateTime.parse("2020-09-06 11:00:23", CommonConstant.FORMATTER));
        mvc.perform(post("/hit")
                        .content(objectMapper.writeValueAsString(hitDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", is("app must be not blank")));
    }

    @Test
    void addHit_whenWrongDateFuture() throws Exception {
        HitDto hitDto = new HitDto(null, "ewm-main-service", "/events/1", "192.163.0.1", LocalDateTime.parse("2035-09-06 00:00:23", CommonConstant.FORMATTER));
        mvc.perform(post("/hit")
                        .content(objectMapper.writeValueAsString(hitDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", is("time must be in the past")));
    }


    @Test
    void getStatistic_whenOk() throws Exception {
        StatisticDto statisticDto = new StatisticDto("ewm-main-service", "/events/1", 2);
        LocalDateTime dateStart = LocalDateTime.of(2020, 5, 5, 0, 0, 0);
        LocalDateTime dateEnd = LocalDateTime.of(2035, 5, 5, 0, 0, 0);
        when(service.getStatistic(dateStart, dateEnd, List.of("/events/1"), true)).thenReturn(List.of(statisticDto));
        when(statsMapper.fromInterface(ArgumentMatchers.any())).thenReturn(statisticDto);
        mvc.perform(get("/stats")
                        .param("start", "2020-05-05 00:00:00")
                        .param("end", "2035-05-05 00:00:00")
                        .param("uris", "/events/1")
                        .param("unique", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].app", is("ewm-main-service")))
                .andExpect(jsonPath("$[0].hits", is(2)));
    }

    @Test
    void getStatistic_whenWrongStart() throws Exception {

        mvc.perform(get("/stats")
                        .param("start", "2020-05-05 00-00:00")
                        .param("end", "2035-05-05 00:00:00")
                        .param("uris", "/events/1")
                        .param("unique", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getStatistic_whenStartAfterEnd() throws Exception {
        mvc.perform(get("/stats")
                        .param("start", "2021-05-05 00:00:00")
                        .param("end", "2020-05-05 00:00:00")
                        .param("uris", "/events/1")
                        .param("unique", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", is("Wrong period")));
    }

    @Test
    void getStatistic_whenUrisEmpty() throws Exception {
        mvc.perform(get("/stats")
                        .param("start", "2020-05-05 00:00:00")
                        .param("end", "2035-05-05 00:00:00")
                        .param("uris", "  ")
                        .param("unique", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(0)));
    }

    @Test
    void getStatistic() throws Exception {
        StatisticDto statisticDto = new StatisticDto("ewm-main-service", "/events/1", 2);
        LocalDateTime dateStart = LocalDateTime.of(2020, 5, 5, 0, 0, 0);
        LocalDateTime dateEnd = LocalDateTime.of(2035, 5, 5, 0, 0, 0);
        when(service.getStatistic(dateStart, dateEnd, List.of("/events/1"), true)).thenReturn(List.of(statisticDto));
        when(statsMapper.fromInterface(ArgumentMatchers.any())).thenReturn(statisticDto);
        mvc.perform(get("/stats")
                        .param("start", "2020-05-05 00:00:00")
                        .param("end", "2035-05-05 00:00:00")
                        .param("uris", "/events/1")
                        .param("unique", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].app", is("ewm-main-service")))
                .andExpect(jsonPath("$[0].hits", is(2)));
    }

    @Test
    void getStatistic_whenUniqueDefault() throws Exception {
        StatisticDto statisticDto = new StatisticDto("ewm-main-service", "/events/1", 2);
        LocalDateTime dateStart = LocalDateTime.of(2020, 5, 5, 0, 0, 0);
        LocalDateTime dateEnd = LocalDateTime.of(2035, 5, 5, 0, 0, 0);
        when(service.getStatistic(dateStart, dateEnd, List.of("/events/1"), false)).thenReturn(List.of(statisticDto));
        when(statsMapper.fromInterface(ArgumentMatchers.any())).thenReturn(statisticDto);
        mvc.perform(get("/stats")
                        .param("start", "2020-05-05 00:00:00")
                        .param("end", "2035-05-05 00:00:00")
                        .param("uris", "   ,/events/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].app", is("ewm-main-service")))
                .andExpect(jsonPath("$[0].hits", is(2)));
    }
}