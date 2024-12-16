package ru.yandex.practicum.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestClient;
import ru.yandex.practicum.SearchStats;
import ru.yandex.practicum.dto.CreateStatsDto;
import ru.yandex.practicum.dto.StatCountHitsDto;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsClientImpl implements StatsClient {
    private final String statsPath;
    private final String hitPath;
    private final RestClient restClient;

    public List<StatCountHitsDto> get(final SearchStats searchStats) {
        Object[] listObj = restClient.get()
                .uri(uriBuilder -> uriBuilder.path(statsPath)
                        .queryParam("start", searchStats.getStart())
                        .queryParam("end", searchStats.getEnd())
                        .queryParam("uris", searchStats.getUris())
                        .queryParam("unique", searchStats.isUnique())
                        .build())
                .retrieve()
                .body(Object[].class);

        if (ObjectUtils.isEmpty(listObj)) {
            return List.of();
        }

        ObjectMapper mapper = new ObjectMapper();
        return Arrays.stream(listObj)
                .map(object -> mapper.convertValue(object, StatCountHitsDto.class))
                .toList();
    }

    @Override
    public ResponseEntity<Void> save(final CreateStatsDto request) {
        return restClient.post()
                .uri(hitPath)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }
}
