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
import java.util.Arrays;
import java.util.List;

public class StatsClient {
    private RestTemplate rest;
    private final String baseUrl;

    public StatsClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.rest = new RestTemplateBuilder().uriTemplateHandler(new DefaultUriBuilderFactory("http://localhost:9090/"))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }

    public List<StatisticDto> get(String start, String end, List<String> uris, Boolean unique) throws UnsupportedEncodingException {

        String urisAsString = uris.get(0);
        for (int i = 1; i < uris.size(); i++) {
            urisAsString = urisAsString + "," + uris.get(i);
        }
        StringBuilder paramsBuilder = new StringBuilder("?");
        paramsBuilder.append("start=");
        paramsBuilder.append(URLEncoder.encode(start, StandardCharsets.UTF_8.toString()));
        paramsBuilder.append("&end=");
        paramsBuilder.append(URLEncoder.encode(end, StandardCharsets.UTF_8.toString()));
        paramsBuilder.append("&uris=");
        paramsBuilder.append(URLEncoder.encode(urisAsString, StandardCharsets.UTF_8.toString()));
        paramsBuilder.append("&unique=");
        paramsBuilder.append(unique.toString());
        String params = paramsBuilder.toString();

        RequestEntity request = new RequestEntity<>(defaultHeaders(), HttpMethod.GET, URI.create(baseUrl + "/stats" + params));
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
