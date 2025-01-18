package ru.yandex.practicum.api.in.grpc.service;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.yandex.practicum.api.out.service.UserActionServiceOut;
import ru.yandex.practicum.grpc.collector.user.UserActionProto;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserActionServiceInImpl implements UserActionServiceIn {
    private final ConversionService conversionService;
    private final UserActionServiceOut userActionServiceOut;

    @Value("${collector.topic.stats.user-actions.v1}")
    private String userActionsTopic;

    @Override
    public void collectUserAction(UserActionProto request, StreamObserver<Empty> responseObserver) {
        log.info("Collect user action proto- {}", request);
        UserActionAvro userAction = conversionService.convert(request, UserActionAvro.class);
        log.info("Collect user action convert proto to avro - {} - {}", request,userAction);
        userActionServiceOut.sendMessageKafka(userActionsTopic, userAction);
    }
}
