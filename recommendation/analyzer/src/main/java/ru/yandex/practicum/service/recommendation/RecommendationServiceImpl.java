package ru.yandex.practicum.service.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.recommendation.InteractionsCountRequestProto;
import ru.yandex.practicum.grpc.recommendation.RecommendedEventProto;
import ru.yandex.practicum.grpc.recommendation.SimilarEventsRequestProto;
import ru.yandex.practicum.grpc.recommendation.UserPredictionsRequestProto;
import ru.yandex.practicum.model.EventSimilarity;
import ru.yandex.practicum.model.UserAction;
import ru.yandex.practicum.repository.EventSimilarityRepository;
import ru.yandex.practicum.repository.UserActionRepository;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {
    private final EventSimilarityRepository eventSimilarityRepository;
    private final UserActionRepository userActionRepository;

    @Override
    public List<RecommendedEventProto> getRecommendationsForUser(UserPredictionsRequestProto request) {
        int userId = request.getUserId();
        int maxResults = request.getMaxResults();

        //  Get recently interacted events by user
        List<Integer> recentlyInteractedEvents = userActionRepository
                .findRecentInteractionsByUser(userId)
                .stream()
                .limit(maxResults)
                .map(UserAction::getEventId)
                .toList();

        if (recentlyInteractedEvents.isEmpty()) {
            return Collections.emptyList();
        }

        // Find similar events the user hasn’t interacted with
        List<RecommendedEventProto> recommendations = recentlyInteractedEvents
                .stream()
                .flatMap(eventId -> eventSimilarityRepository.findRawSimilarEvents(eventId)
                        .stream())
                .filter(event -> !userActionRepository
                        .hasUserInteractedWithEvent(userId,  event.getEventA()))
                .sorted(Comparator.comparingDouble(EventSimilarity::getScore).reversed())
                .limit(maxResults)
                .map(eventSimilarity -> RecommendedEventProto.newBuilder()
                        .setEventId(eventSimilarity.getEventA())
                        .setScore(eventSimilarity.getScore())
                        .build())
                .collect(Collectors.toList());

        return recommendations;
    }

    @Override
    public List<RecommendedEventProto> getSimilarEvents(SimilarEventsRequestProto request) {
        int eventId = request.getEventId();
        int userId = request.getUserId();
        int maxResults = request.getMaxResults();

        // Get all similar events for the given event ID
        List<RecommendedEventProto> similarEvents = eventSimilarityRepository
                .findRawSimilarEvents(eventId)
                .stream()
                .map(eventSimilarity -> RecommendedEventProto.newBuilder()
                        .setEventId(eventSimilarity.getEventA())
                        .setScore(eventSimilarity.getScore())
                        .build())
                .toList();

        // Exclude events the user has already interacted with
        List<Integer> userInteractions = userActionRepository.findInteractedEventsByUser(userId);
        similarEvents = similarEvents
                .stream()
                .filter(event -> !userInteractions.contains(event.getEventId()))
                .sorted(Comparator.comparingDouble(RecommendedEventProto::getScore).reversed())
                .limit(maxResults)
                .toList();

        return similarEvents;
    }

    @Override
    public List<RecommendedEventProto> getInteractionsCount(InteractionsCountRequestProto request) {
        List<Integer> eventIds = request.getEventIdList();

        // Fetch interaction counts for each event
        return eventIds
                .stream()
                .map(eventId -> {
                    int interactionCount = userActionRepository.countInteractionsForEvent(eventId);
                    return RecommendedEventProto.newBuilder()
                            .setEventId(eventId)
                            .setScore(interactionCount)
                            .build();
                })
                .toList();
    }
}
