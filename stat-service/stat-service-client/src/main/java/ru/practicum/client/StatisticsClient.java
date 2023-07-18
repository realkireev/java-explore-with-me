package ru.practicum.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.HitRequestDto;
import ru.practicum.dto.HitResponseDto;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class StatisticsClient {
    private final RestTemplate rest;
    private final String statServerPath;
    private final HttpHeaders headers = new HttpHeaders();
    private final ObjectMapper objectMapper;

    public StatisticsClient(RestTemplateBuilder builder, ObjectMapper objectMapper,
                            @Value("${stat.server.path}") String statServerPath) {
        this.rest = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(statServerPath))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();

        this.statServerPath = statServerPath;
        this.objectMapper = objectMapper;

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
    }

    public void saveStatistics(String app, String uri, String ip)
            throws JsonProcessingException {
        HitRequestDto hitRequestDto = HitRequestDto.builder()
                .app(app)
                .uri(uri)
                .ip(ip)
                .timestamp(LocalDateTime.now())
                .build();

        String json = objectMapper.writeValueAsString(hitRequestDto);
        HttpEntity<String> requestEntity = new HttpEntity<>(json, headers);

        invokeHttp(HttpMethod.POST, "/hit", requestEntity);
    }

    public ResponseEntity<List<HitResponseDto>> getStatistics(String start, String end, List<String> uris,
                                                              Boolean unique) {
        StringBuilder path = new StringBuilder("/stats");
        path.append("?start=").append(start).append("&end=").append(end);

        if (uris != null) {
            path.append("&uris=").append(uris);
        }

        if (unique != null) {
            path.append("&unique=").append(unique);
        }

        return invokeHttp(HttpMethod.GET, path.toString(), null);
    }

    private ResponseEntity<List<HitResponseDto>> invokeHttp(HttpMethod httpMethod, String path,
                                                            HttpEntity<String> requestEntity) {
        ResponseEntity<List<HitResponseDto>> statServerResponse = null;
        ParameterizedTypeReference<List<HitResponseDto>> responseType = new ParameterizedTypeReference<>() {};

        try {
            statServerResponse = rest.exchange(statServerPath + path, httpMethod, requestEntity, responseType);
        } catch (HttpStatusCodeException e) {
            e.printStackTrace();
        }

        return statServerResponse;
    }
}
