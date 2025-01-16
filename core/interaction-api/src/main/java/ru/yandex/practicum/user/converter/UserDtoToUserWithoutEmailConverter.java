package ru.yandex.practicum.user.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.user.model.User;
import ru.yandex.practicum.user.model.dto.UserWithoutEmailDto;

@Component
public class UserDtoToUserWithoutEmailConverter implements Converter<UserWithoutEmailDto, User> {
    @Override
    public User convert(final UserWithoutEmailDto src) {
        return User.builder()
                .id(src.id())
                .name(src.name())
                .build();
    }
}
