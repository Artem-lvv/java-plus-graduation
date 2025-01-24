package ru.yandex.practicum.service;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import ru.yandex.practicum.grpc.collector.user.UserActionProto;

public interface CollectorService {
    void collectUserAction(UserActionProto request, StreamObserver<Empty> responseObserver);
}
