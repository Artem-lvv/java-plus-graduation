package ru.yandex.practicum.api.out.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaTemplateOutComponent {
    private final KafkaTemplate<String, EventSimilarityAvro> kafkaTemplate;

    public void sendMessageKafka(String topic, EventSimilarityAvro message) {
        log.info("sendMessageKafka topic {} message {}", topic, message);
        kafkaTemplate.send(topic, message);
    }
}
