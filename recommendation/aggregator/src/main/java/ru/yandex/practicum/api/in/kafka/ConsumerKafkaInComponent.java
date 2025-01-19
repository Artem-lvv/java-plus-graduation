package ru.yandex.practicum.api.in.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.yandex.practicum.service.AggregatorService;

@Component
@RequiredArgsConstructor
public class ConsumerKafkaInComponent {
    private final AggregatorService aggregatorService;

    @KafkaListener(topics = "stats.user-actions.v1", groupId = "aggregator-group")
    public void consumeUserActionAvro(UserActionAvro userActionAvro) {
        aggregatorService.consumeUserActionAvro(userActionAvro);
    }
}
