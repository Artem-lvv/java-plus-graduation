package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.grpc.recommendation.RecommendedEventProto;
import ru.yandex.practicum.model.EventSimilarity;

import java.util.List;
import java.util.Optional;

public interface EventSimilarityRepository extends JpaRepository<EventSimilarity, Long> {
    Optional<EventSimilarity> findByEventAAndEventB(int eventA, int eventB);

    List<EventSimilarity> findAllByEventAOrderByScoreDesc(int eventA);

    @Query("SELECT new RecommendedEventProto(e.eventB, e.score) " +
            "FROM EventSimilarity e " +
            "WHERE e.eventA = :eventId " +
            "ORDER BY e.score DESC")
    List<RecommendedEventProto> findSimilarEvents(int eventId);
}