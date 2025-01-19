package ru.yandex.practicum.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.yandex.practicum.grpc.collector.user.UserActionProto;

@Component
public class UserActionProtoToAvroConverter implements Converter<UserActionProto, UserActionAvro> {
    @Override
    public UserActionAvro convert(UserActionProto source) {
        return UserActionAvro.newBuilder()
                .setUserId((int) source.getUserId())
                .setEventId((int) source.getEventId())
                .setActionType(switch (source.getActionType()) {
                    case ACTION_VIEW -> ActionTypeAvro.VIEW;
                    case ACTION_REGISTER -> ActionTypeAvro.REGISTER;
                    case ACTION_LIKE -> ActionTypeAvro.LIKE;
                    case UNRECOGNIZED -> null;})
                .setTimestamp((source.getTimestamp().getSeconds() * 1000)
                        + (source.getTimestamp().getNanos() / 1000000))
                .build();
    }
}
