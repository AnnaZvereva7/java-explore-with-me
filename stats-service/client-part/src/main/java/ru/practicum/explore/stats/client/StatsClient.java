package ru.practicum.explore.stats.client;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.explore.stats.dto.HitDto;
import ru.practicum.explore.stats.dto.StatisticDto;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class StatsClient {
    private final RestTemplate rest;
    private final String baseUrl;
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StatsClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.rest = new RestTemplateBuilder().uriTemplateHandler(new DefaultUriBuilderFactory(baseUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }

    public List<StatisticDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) throws UnsupportedEncodingException {
        StringBuilder urisPart = new StringBuilder();
        urisPart.append(uris.get(0));
        for (int i = 1; i < uris.size(); i++) {
            urisPart.append(",");
            urisPart.append(uris.get(i));
        }
        String urisAsString = urisPart.toString();
        String params = "?" + "start=" +
                URLEncoder.encode(start.format(FORMATTER), StandardCharsets.UTF_8) +
                "&end=" +
                URLEncoder.encode(end.format(FORMATTER), StandardCharsets.UTF_8) +
                "&uris=" +
                URLEncoder.encode(urisAsString, StandardCharsets.UTF_8) +
                "&unique=" +
                unique.toString();

        RequestEntity<Object> request = new RequestEntity<>(defaultHeaders(), HttpMethod.GET, URI.create(baseUrl + "/stats" + params));
        ResponseEntity<StatisticDto[]> responseEntity = rest.exchange(request, StatisticDto[].class);
        return Arrays.asList(responseEntity.getBody());
    }

    public boolean post(HitDto hitDto) {
        RequestEntity<HitDto> request = new RequestEntity<>(hitDto, defaultHeaders(), HttpMethod.POST, URI.create(baseUrl + "/hit"));
        ResponseEntity<Void> responseEntity = rest.exchange(request, Void.class);
        return true;
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

}
