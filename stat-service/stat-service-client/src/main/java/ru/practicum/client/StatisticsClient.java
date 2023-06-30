package ru.practicum.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
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

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class StatisticsClient {
    private final RestTemplate rest;
    private final String statServerPath;
    private final HttpHeaders headers = new HttpHeaders();

    public StatisticsClient(RestTemplateBuilder builder, @Value("${stat.server.path}") String statServerPath ) {
        this.rest = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(statServerPath))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();

        this.statServerPath = statServerPath;

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
    }

    public ResponseEntity<Object> saveStatistics(String app, String uri, String ip, LocalDateTime timestamp)
            throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        HitRequestDto hitRequestDto = new HitRequestDto();
        hitRequestDto.setApp(app);
        hitRequestDto.setUri(uri);
        hitRequestDto.setIp(ip);
        hitRequestDto.setTimestamp(timestamp);

        String json = objectMapper.writeValueAsString(hitRequestDto);
        HttpEntity<String> requestEntity = new HttpEntity<>(json, headers);

        ResponseEntity<Object> statServerResponse;

        try {
            statServerResponse = rest.exchange(statServerPath, HttpMethod.POST, requestEntity, Object.class);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return statServerResponse;
    }
}
