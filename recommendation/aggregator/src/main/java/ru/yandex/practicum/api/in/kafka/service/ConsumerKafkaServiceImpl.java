package ru.yandex.practicum.api.in.kafka.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.UserActionAvro;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsumerKafkaServiceImpl implements ConsumerKafkaService {

    @Override
    public void consumeUserActionAvro(UserActionAvro userActionAvro) {
        log.info("Consume user action avro: {}", userActionAvro);


    }
}
