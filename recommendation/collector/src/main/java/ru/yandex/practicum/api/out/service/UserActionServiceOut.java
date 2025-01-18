package ru.yandex.practicum.api.out.service;

import org.apache.avro.specific.SpecificRecordBase;

public interface UserActionServiceOut {
    void sendMessageKafka(String topic, SpecificRecordBase message);
}
