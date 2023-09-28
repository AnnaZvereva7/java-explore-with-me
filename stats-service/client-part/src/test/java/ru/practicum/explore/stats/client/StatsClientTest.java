package ru.practicum.explore.stats.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpClientErrorException;
import ru.practicum.explore.stats.dto.HitDto;
import ru.practicum.explore.stats.dto.StatisticDto;
import ru.practicum.explore.stats.dto.StatisticDtoInterface;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;

class StatsClientTest {

    public WireMockServer wireMockServer = new WireMockServer(9090);
    private StatsClient client;
    private final ObjectMapper objectMapper = new ObjectMapper();
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @BeforeEach
    void setUp() {
        wireMockServer.start();
        client = new StatsClient(wireMockServer.baseUrl());
    }

    @AfterEach
    void turnDown() {
        wireMockServer.stop();
    }

    @Test
    void getStats() throws JsonProcessingException, UnsupportedEncodingException {
        StatisticDtoInterface statisticDtoInterface = new StatisticDto("ewm-main-service", "/events/1", 2);
        List<StatisticDtoInterface> result = List.of(statisticDtoInterface);

        String encodeStart = URLEncoder.encode("2020-05-05 00:00:00", StandardCharsets.UTF_8);
        String encodeEnd = URLEncoder.encode("2035-05-05 00:00:00", StandardCharsets.UTF_8);
        String encodeUris = URLEncoder.encode("&uris=/events/1&uris=/events/2", StandardCharsets.UTF_8);

        configureFor("localhost", 9090);
        stubFor(get(urlEqualTo("/stats?start=" + encodeStart + "&end=" + encodeEnd + encodeUris + "&unique=true"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withHeader("Accept", equalTo("application/json"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(result))));
        List<StatisticDto> actualResult = client.get(LocalDateTime.parse("2020-05-05 00:00:00", FORMATTER),
                LocalDateTime.parse("2035-05-05 00:00:00", FORMATTER), List.of("/events/1", "/events/2"), true);
        assertEquals(result, actualResult);
    }

    @Test
    void getStats_whenError() throws JsonProcessingException, UnsupportedEncodingException {
        String encodeStart = URLEncoder.encode("2020-05-05 00:00:00", StandardCharsets.UTF_8);
        String encodeEnd = URLEncoder.encode("2035-05-05 00:00:00", StandardCharsets.UTF_8);
        String encodeUris = URLEncoder.encode("&uris=/events/1&uris=/events/2", StandardCharsets.UTF_8);

        configureFor("localhost", 9090);
        stubFor(get(urlEqualTo("/stats?start=" + encodeStart + "&end=" + encodeEnd + encodeUris + "&unique=true"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withHeader("Accept", equalTo("application/json"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("error")));
        Throwable thrown = catchThrowable(() -> {
            client.get(LocalDateTime.parse("2020-05-05 00:00:00", FORMATTER),
                    LocalDateTime.parse("2035-05-05 00:00:00", FORMATTER), List.of("/events/1", "/events/2"), true);
        });
        assertThat(thrown).isInstanceOf(HttpClientErrorException.class);
        assertEquals(thrown.getMessage(), "400 Bad Request: \"error\"");
    }

    @Test
    void postHit() {
        configureFor("localhost", 9090);
        stubFor(post(urlEqualTo("/hit"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withHeader("Accept", equalTo("application/json"))
                .withRequestBody(matchingJsonPath("$.app", containing("ewm-main-service")))
                .withRequestBody(matchingJsonPath("$.uri", containing("/events/1")))
                .withRequestBody(matchingJsonPath("$.ip", containing("192.163.0.1")))
                .withRequestBody(matchingJsonPath("$.timestamp", containing("2022-09-06 11:00:23")))
                .willReturn(aResponse()
                        .withStatus(200)));
        client.post(new HitDto(null, "ewm-main-service",
                "/events/1", "192.163.0.1", LocalDateTime.parse("2022-09-06 11:00:23", FORMATTER)));
        //  assertEquals(true, result);
    }

    @Test
    void postHit_whenError() {
        configureFor("localhost", 9090);
        stubFor(post(urlEqualTo("/hit"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withHeader("Accept", equalTo("application/json"))
                .withRequestBody(matchingJsonPath("$.app", containing("ewm-main-service")))
                .withRequestBody(matchingJsonPath("$.uri", containing("/events/1")))
                .withRequestBody(matchingJsonPath("$.ip", containing("192.163.0.1")))
                .withRequestBody(matchingJsonPath("$.timestamp", containing("2022-09-06 11:00:23")))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("error")));

        Throwable thrown = catchThrowable(() -> {
            client.post(new HitDto(null, "ewm-main-service",
                    "/events/1", "192.163.0.1", LocalDateTime.parse("2022-09-06 11:00:23", FORMATTER)));
        });
        assertThat(thrown).isInstanceOf(HttpClientErrorException.class);
        assertEquals(thrown.getMessage(), "400 Bad Request: \"error\"");
    }
}