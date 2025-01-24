package ru.yandex.practicum.api.in.grpc;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.apache.commons.lang.SerializationException;
import org.apache.kafka.common.errors.ProducerFencedException;
import ru.yandex.practicum.grpc.recommendation.InteractionsCountRequestProto;
import ru.yandex.practicum.grpc.recommendation.RecommendationsControllerGrpc;
import ru.yandex.practicum.grpc.recommendation.RecommendedEventProto;
import ru.yandex.practicum.grpc.recommendation.SimilarEventsRequestProto;
import ru.yandex.practicum.grpc.recommendation.UserPredictionsRequestProto;
import ru.yandex.practicum.service.recommendation.RecommendationService;

import java.util.List;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class RecommendationControllerGrpcExt extends RecommendationsControllerGrpc.RecommendationsControllerImplBase {
    private final RecommendationService recommendationService;

    @Override
    public void getRecommendationsForUser(UserPredictionsRequestProto request,
                                          StreamObserver<RecommendedEventProto> responseObserver) {
        try {
            List<RecommendedEventProto> recommendationsForUser = recommendationService
                    .getRecommendationsForUser(request);

            recommendationsForUser.forEach(responseObserver::onNext);

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

    @Override
    public void getSimilarEvents(SimilarEventsRequestProto request,
                                 StreamObserver<RecommendedEventProto> responseObserver) {
        try {
            List<RecommendedEventProto> similarEvents = recommendationService
                    .getSimilarEvents(request);

            similarEvents.forEach(responseObserver::onNext);

            responseObserver.onCompleted();
        } catch (ProducerFencedException | SerializationException  e) {
            log.error(e.getMessage(), e);

            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
    }

    @Override
    public void getInteractionsCount(InteractionsCountRequestProto request,
                                     StreamObserver<RecommendedEventProto> responseObserver) {
        try {
            List<RecommendedEventProto> interactionsCount = recommendationService
                    .getInteractionsCount(request);

            interactionsCount.forEach(responseObserver::onNext);

            responseObserver.onCompleted();
        } catch (ProducerFencedException | SerializationException  e) {
            log.error(e.getMessage(), e);

            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
    }
}
