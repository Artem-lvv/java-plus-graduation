package ru.yandex.practicum.api.in.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.yandex.practicum.service.analyzer.AnalyzerService;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConsumerKafkaInComponent {
    private final AnalyzerService analyzerService;

    @KafkaListener(topics = "stats.user-actions.v1", groupId = "analyzer-group")
    public void consumeUserActionAvro(UserActionAvro userActionAvro) {
        log.info("consumeUserActionAvro UserActionAvro: {}", userActionAvro);
        analyzerService.saveUserAction(userActionAvro);
    }

    @KafkaListener(topics = "stats.events-similarity.v1", groupId = "analyzer-group")
    public void consumeEventSimilarity(EventSimilarityAvro eventSimilarityAvro) {
        log.info("consumeEventSimilarity EventSimilarityAvro: {}", eventSimilarityAvro);
        analyzerService.saveEventSimilarity(eventSimilarityAvro);
    }
}
