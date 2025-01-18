package ru.yandex.practicum.api.in.kafka.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.yandex.practicum.api.in.kafka.service.ConsumerKafkaService;

@Component
@RequiredArgsConstructor
public class ConsumerKafka {
    private final ConsumerKafkaService consumerKafkaService;

    @KafkaListener(topics = "stats.user-actions.v1", groupId = "aggregator-group")
    public void consumeUserActionAvro(UserActionAvro userActionAvro) {
        consumerKafkaService.consumeUserActionAvro(userActionAvro);
    }
}
