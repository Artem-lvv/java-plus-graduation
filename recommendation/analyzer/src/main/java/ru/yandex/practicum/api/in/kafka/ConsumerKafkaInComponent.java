package ru.yandex.practicum.api.in.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.UserActionAvro;

@Component
@RequiredArgsConstructor
public class ConsumerKafkaInComponent {

    @KafkaListener(topics = "stats.user-actions.v1", groupId = "aggregator-group")
    public void consumeUserActionAvro(UserActionAvro userActionAvro) {
//        aggregatorService.consumeUserActionAvro(userActionAvro);
    }
}
