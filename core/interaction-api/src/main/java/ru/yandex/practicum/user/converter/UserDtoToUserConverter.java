package ru.yandex.practicum.user.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.user.model.User;
import ru.yandex.practicum.user.model.dto.CreateUserDto;
import ru.yandex.practicum.user.model.dto.UserDto;

@Component
public class UserDtoToUserConverter implements Converter<UserDto, User> {
    @Override
    public User convert(final UserDto src) {
        return User.builder()
                .id(src.id())
                .email(src.email())
                .name(src.name())
                .build();
    }
}
