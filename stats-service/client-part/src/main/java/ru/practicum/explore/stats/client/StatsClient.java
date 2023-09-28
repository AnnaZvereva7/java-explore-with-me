package ru.practicum.explore.stats.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.explore.stats.dto.HitDto;
import ru.practicum.explore.stats.dto.StatisticDto;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
public class StatsClient {
    private final RestTemplate rest;
    private final String baseUrl;
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd%20HH:mm:ss");

    public StatsClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.rest = new RestTemplateBuilder().uriTemplateHandler(new DefaultUriBuilderFactory(baseUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }

    public List<StatisticDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) throws UnsupportedEncodingException {
        StringBuilder urisPart = new StringBuilder();

        for (String uri : uris) {
            urisPart.append("&uris=");
            urisPart.append(uri);
        }
        String urisAsString = urisPart.toString();
//        String params = "?" + "start=" +
//                URLEncoder.encode(start.format(FORMATTER), StandardCharsets.UTF_8) +
//                "&end=" +
//                URLEncoder.encode(end.format(FORMATTER), StandardCharsets.UTF_8) +
//                URLEncoder.encode(urisAsString, StandardCharsets.UTF_8) +
//                "&unique=" +
//                unique.toString();

        String params = "?" + "start=" + start.format(FORMATTER) + "&end=" + end.format(FORMATTER) + urisAsString + "&unique=" + unique.toString();

        RequestEntity<Object> request = new RequestEntity<>(defaultHeaders(), HttpMethod.GET, URI.create(baseUrl + "/stats" + params));
        ResponseEntity<List<StatisticDto>> responseEntity = rest.exchange(request, new ParameterizedTypeReference<List<StatisticDto>>() {
        });
        List<StatisticDto> result = responseEntity.getBody();
        log.info("result {}", result);
        return result;
    }

    public void post(HitDto hitDto) {
        RequestEntity<HitDto> request = new RequestEntity<>(hitDto, defaultHeaders(), HttpMethod.POST, URI.create(baseUrl + "/hit"));
        ResponseEntity<HitDto> responseEntity = rest.exchange(request, HitDto.class);
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

}
