package com.github.artemlv.ewm.event.config;

import com.github.artemlv.ewm.exception.type.ConflictException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestClient;
import ru.yandex.practicum.client.StatsClient;
import ru.yandex.practicum.client.StatsClientImpl;

@Configuration
public class WebClientConfig {
    @Value("${stats-server.url}")
    private String baseUrl;
    @Value("${stats-server.hit}")
    private String hit;
    @Value("${stats-server.stats}")
    private String stats;

    @Bean
    public StatsClient statsClient() {
        if (ObjectUtils.isEmpty(stats) || ObjectUtils.isEmpty(hit) || ObjectUtils.isEmpty(baseUrl)) {
            throw new ConflictException("stats-server or hit or base url is empty");
        }

        return new StatsClientImpl(stats, hit,
                RestClient.builder()
                        .baseUrl(baseUrl)
                        .build()
        );
    }
}
