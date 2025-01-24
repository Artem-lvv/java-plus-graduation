package ru.yandex.practicum.api.outData.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaTemplateOutComponent {
    private final KafkaTemplate<String, SpecificRecordBase> kafkaTemplate;

    public void sendMessageKafka(String topic, SpecificRecordBase message) {
        log.info("sendMessageKafka topic {} message {}", topic, message);
        kafkaTemplate.send(topic, message);
    }
}
