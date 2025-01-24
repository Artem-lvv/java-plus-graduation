package ru.yandex.practicum.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.yandex.practicum.model.EventSimilarity;

import java.time.Instant;

@Component
public class EventSimilarityAvroToEntityConverter implements Converter<EventSimilarityAvro, EventSimilarity> {
    @Override
    public EventSimilarity convert(EventSimilarityAvro src) {
        return EventSimilarity.builder()
                .eventA(src.getEventA())
                .eventB(src.getEventB())
                .score(src.getScore())
                .timestamp(Instant.ofEpochSecond(src.getTimestamp()))
                .build();
    }
}
