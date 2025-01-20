package ru.yandex.practicum.service;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.yandex.practicum.api.outData.kafka.KafkaTemplateOutComponent;
import ru.yandex.practicum.grpc.collector.user.UserActionProto;

@Slf4j
@Service
@RequiredArgsConstructor
public class CollectorServiceImpl implements CollectorService {
    private final ConversionService conversionService;
    private final KafkaTemplateOutComponent kafkaTemplateOutComponent;

    @Value("${collector.topic.stats.user-actions.v1:stats.user-actions.v1}")
    private String statsUserActionsV1Topic;

    @Override
    public void collectUserAction(UserActionProto request, StreamObserver<Empty> responseObserver) {
        log.info("Collect user action proto- {}", request);
        UserActionAvro userActionAvro = conversionService.convert(request, UserActionAvro.class);
        log.info("Collect user action convert proto to avro - {} - {}", request,userActionAvro);
        kafkaTemplateOutComponent.sendMessageKafka(statsUserActionsV1Topic, userActionAvro);
    }
}
