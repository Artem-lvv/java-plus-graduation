package ru.yandex.practicum.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.yandex.practicum.model.ActionType;
import ru.yandex.practicum.model.UserAction;

import java.time.Instant;

@Component
public class UserActionAvroToEntityConverter implements Converter<UserActionAvro, UserAction> {
    @Override
    public UserAction convert(UserActionAvro src) {
        return UserAction.builder()
                .userId(src.getUserId())
                .eventId(src.getEventId())
                .actionType(ActionType.valueOf(src.getActionType().name()))
                .timestamp(Instant.ofEpochSecond(src.getTimestamp()))
                .build();
    }
}
