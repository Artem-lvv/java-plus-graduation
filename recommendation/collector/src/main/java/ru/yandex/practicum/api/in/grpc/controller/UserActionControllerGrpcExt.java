package ru.yandex.practicum.api.in.grpc.controller;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.apache.commons.lang.SerializationException;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.errors.ProducerFencedException;
import ru.yandex.practicum.grpc.collector.controller.UserActionControllerGrpc;
import ru.yandex.practicum.grpc.collector.user.UserActionProto;
import ru.yandex.practicum.service.CollectorService;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class UserActionControllerGrpcExt extends UserActionControllerGrpc.UserActionControllerImplBase {
    private final CollectorService userActionService;

    @Override
    public void collectUserAction(UserActionProto request, StreamObserver<Empty> responseObserver) {
        try {
            userActionService.collectUserAction(request, responseObserver);

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (ProducerFencedException | SerializationException e) {
            log.error(e.getMessage(), e);

            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
    }
}
