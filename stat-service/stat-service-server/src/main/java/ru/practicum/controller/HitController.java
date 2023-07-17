package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.HitRequestDto;
import ru.practicum.dto.HitResponseDto;
import ru.practicum.service.HitService;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class HitController {
    private final HitService hitService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveHit(@NotNull @RequestBody HitRequestDto hitRequestDto) {
        log.debug("POST /hit - Saving hit: {}", hitRequestDto);

        hitService.saveHit(hitRequestDto);
    }

    @GetMapping("/stats")
    public List<HitResponseDto> getStatistics(
            @NotNull @RequestParam LocalDateTime start,
            @NotNull @RequestParam LocalDateTime end,
            @RequestParam(defaultValue = "") List<String> uris,
            @RequestParam(defaultValue = "false") Boolean unique) {
        log.debug("GET /stats - Getting statistics with params: start={}, end={}, uris={}, unique={}", start,
                end, uris, unique);

        return hitService.getStatistics(start, end, uris, unique);
    }
}
