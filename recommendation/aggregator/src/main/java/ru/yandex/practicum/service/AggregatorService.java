package ru.yandex.practicum.service;

import ru.practicum.ewm.stats.avro.UserActionAvro;

public interface AggregatorService {
    void consumeUserActionAvro(UserActionAvro userActionAvro);
}
