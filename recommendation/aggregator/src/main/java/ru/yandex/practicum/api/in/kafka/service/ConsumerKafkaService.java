package ru.yandex.practicum.api.in.kafka.service;

import ru.practicum.ewm.stats.avro.UserActionAvro;

public interface ConsumerKafkaService {
    void consumeUserActionAvro(UserActionAvro userActionAvro);
}
