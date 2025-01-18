package ru.yandex.practicum.api.in.grpc.service;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import ru.yandex.practicum.grpc.collector.user.UserActionProto;

public interface UserActionServiceIn {
    void collectUserAction(UserActionProto request, StreamObserver<Empty> responseObserver);
}
