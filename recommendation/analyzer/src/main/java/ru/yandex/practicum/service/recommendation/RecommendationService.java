package ru.yandex.practicum.service.recommendation;

import ru.yandex.practicum.grpc.recommendation.InteractionsCountRequestProto;
import ru.yandex.practicum.grpc.recommendation.RecommendedEventProto;
import ru.yandex.practicum.grpc.recommendation.SimilarEventsRequestProto;
import ru.yandex.practicum.grpc.recommendation.UserPredictionsRequestProto;

import java.util.List;

public interface RecommendationService {
    List<RecommendedEventProto> getRecommendationsForUser(UserPredictionsRequestProto request);
    List<RecommendedEventProto> getSimilarEvents(SimilarEventsRequestProto request);
    List<RecommendedEventProto> getInteractionsCount(InteractionsCountRequestProto request);
}
