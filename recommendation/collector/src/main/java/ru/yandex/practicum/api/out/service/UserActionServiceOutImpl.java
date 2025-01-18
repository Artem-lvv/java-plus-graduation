package ru.yandex.practicum.api.out.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserActionServiceOutImpl implements UserActionServiceOut {
    private final KafkaTemplate<String, SpecificRecordBase> kafkaTemplate;

    @Override
    public void sendMessageKafka(String topic, SpecificRecordBase message) {
        log.info("sendMessageKafka topic {} message {}", topic, message);
        kafkaTemplate.send(topic, message);
    }
}
