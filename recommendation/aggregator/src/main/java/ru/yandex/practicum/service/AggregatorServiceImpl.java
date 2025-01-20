package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.yandex.practicum.api.out.kafka.KafkaTemplateOutComponent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class AggregatorServiceImpl implements AggregatorService {
    private final KafkaTemplateOutComponent kafkaTemplate;

    // Storage for user weights
    private final Map<Long, Map<Long, Float>> eventUserWeights = new ConcurrentHashMap<>();
    private final Map<Long, Double> eventTotalWeights = new ConcurrentHashMap<>();
    private final Map<Long, Map<Long, Double>> minWeightsSums = new ConcurrentHashMap<>();

    @Value("${stats.events-similarity.v1:stats.events-similarity.v1}")
    private String statsEventsSimilarityV1;
    private static final float VIEW_WEIGHT = 0.4F;
    private static final float LIKE_WEIGHT = 0.8F;
    private static final float REGISTER_WEIGHT = 1.0F;


    @Override
    public void consumeUserActionAvro(UserActionAvro userActionAvro) {
        log.info("Consume user action avro: {}", userActionAvro);

        float weight = switch (userActionAvro.getActionType()) {
            case VIEW -> VIEW_WEIGHT;
            case REGISTER -> REGISTER_WEIGHT;
            case LIKE -> LIKE_WEIGHT;
        };

        processInteraction(userActionAvro.getEventId(), userActionAvro.getUserId(), weight);
    }

    // Process user interaction with an event
    public void processInteraction(long eventId, long userId, float weight) {
        // Update user weights for the event
        eventUserWeights.computeIfAbsent(eventId, e -> new ConcurrentHashMap<>())
                .merge(userId, weight, Math::max);

        // Recalculate total weight for the event
        recalculateTotalWeight(eventId);

        // Recalculate similarity with other events
        for (Long otherEventId : eventUserWeights.keySet()) {
            if (eventId != otherEventId) {
                updateSimilarity(eventId, otherEventId);
            }
        }
    }

    private void recalculateTotalWeight(long eventId) {
        double totalWeight = eventUserWeights.getOrDefault(eventId, Map.of())
                .values()
                .stream()
                .mapToDouble(Double::valueOf)
                .sum();
        eventTotalWeights.put(eventId, totalWeight);
    }

    private void updateSimilarity(long eventA, long eventB) {
        // Ensure event order
        long first = Math.min(eventA, eventB);
        long second = Math.max(eventA, eventB);

        // Calculate S_min
        double sMin = calculateMinWeightSum(first, second);

        // Calculate cosine similarity
        double similarity = calculateCosineSimilarity(first, second, sMin);

        // Send similarity to Kafka
        sendSimilarityToKafka(first, second, similarity, System.currentTimeMillis());
    }

    private void sendSimilarityToKafka(long eventA, long eventB, double similarity, long timestamp) {
        EventSimilarityAvro message = new EventSimilarityAvro((int) eventA,(int) eventB, (float) similarity, timestamp);
        kafkaTemplate.sendMessageKafka(statsEventsSimilarityV1, message);
    }

    private double calculateMinWeightSum(long eventA, long eventB) {
        Map<Long, Float> usersA = eventUserWeights.getOrDefault(eventA, Map.of());
        Map<Long, Float> usersB = eventUserWeights.getOrDefault(eventB, Map.of());

        double sMin = 0.0;
        for (Map.Entry<Long, Float> entry : usersA.entrySet()) {
            long userId = entry.getKey();
            float weightA = entry.getValue();
            float weightB = usersB.getOrDefault(userId, 0.0f);
            sMin += Math.min(weightA, weightB);
        }

        // Update S_min storage
        minWeightsSums.computeIfAbsent(eventA, e -> new ConcurrentHashMap<>())
                .put(eventB, sMin);

        return sMin;
    }

    private double calculateCosineSimilarity(long eventA, long eventB, double sMin) {
        double totalA = eventTotalWeights.getOrDefault(eventA, 0.0);
        double totalB = eventTotalWeights.getOrDefault(eventB, 0.0);

        // Avoid division by zero
        if (totalA == 0 || totalB == 0) {
            return 0.0;
        }

        return sMin / (Math.sqrt(totalA) * Math.sqrt(totalB));
    }

}
