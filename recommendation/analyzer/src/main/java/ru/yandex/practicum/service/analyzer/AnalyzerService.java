package ru.yandex.practicum.service.analyzer;

import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

public interface AnalyzerService {

    void saveUserAction(UserActionAvro userActionAvro);

    void saveEventSimilarity(EventSimilarityAvro eventSimilarityAvro);
}
