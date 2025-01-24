package ru.yandex.practicum.service.analyzer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.yandex.practicum.converter.EventSimilarityAvroToEntityConverter;
import ru.yandex.practicum.converter.UserActionAvroToEntityConverter;
import ru.yandex.practicum.model.EventSimilarity;
import ru.yandex.practicum.model.UserAction;
import ru.yandex.practicum.repository.EventSimilarityRepository;
import ru.yandex.practicum.repository.UserActionRepository;

import java.time.Instant;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyzerServiceImpl implements AnalyzerService {
    private final EventSimilarityRepository eventSimilarityRepository;
    private final UserActionRepository userActionRepository;
    private final EventSimilarityAvroToEntityConverter eventSimilarityAvroToEntityConverter;
    private final UserActionAvroToEntityConverter userActionAvroToEntityConverter;

    @Override
    public void saveUserAction(UserActionAvro userActionAvro) {
        UserAction userAction = userActionAvroToEntityConverter.convert(userActionAvro);

        userActionRepository.save(userAction);
        log.info("Save user action: {}", userAction);
    }

    @Override
    public void saveEventSimilarity(EventSimilarityAvro eventSimilarityAvro) {
        Optional<EventSimilarity> eventSimilarity = eventSimilarityRepository
                .findByEventAAndEventB(eventSimilarityAvro.getEventA(), eventSimilarityAvro.getEventB());

        if (eventSimilarity.isPresent()) {
            eventSimilarity.get().setScore(eventSimilarityAvro.getScore());
            eventSimilarity.get().setTimestamp(Instant.ofEpochSecond(eventSimilarityAvro.getTimestamp()));

            eventSimilarityRepository.save(eventSimilarity.get());
            log.info("Update and save event similarity: {}", eventSimilarity.get());
        } else {
            EventSimilarity eventSimilarityCreate = eventSimilarityAvroToEntityConverter.convert(eventSimilarityAvro);
            eventSimilarityRepository.save(eventSimilarityCreate);
            log.info("Create and save event similarity: {}", eventSimilarityCreate);
        }
    }
}
