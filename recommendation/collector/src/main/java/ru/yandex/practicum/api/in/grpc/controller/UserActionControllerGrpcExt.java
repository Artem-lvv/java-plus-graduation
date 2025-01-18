package ru.yandex.practicum.api.in.grpc.controller;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.yandex.practicum.api.in.grpc.service.UserActionServiceIn;
import ru.yandex.practicum.grpc.collector.controller.UserActionControllerGrpc;
import ru.yandex.practicum.grpc.collector.user.UserActionProto;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class UserActionControllerGrpcExt extends UserActionControllerGrpc.UserActionControllerImplBase {
    private final UserActionServiceIn userActionService;

    @Override
    public void collectUserAction(UserActionProto request, StreamObserver<Empty> responseObserver) {
        try {
            userActionService.collectUserAction(request, responseObserver);

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error(e.getMessage(), e);

            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
    }
}
