package ru.yandex.practicum.user.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.user.model.User;
import ru.yandex.practicum.user.model.dto.UserWithoutEmailDto;

@Component
public class UserToUserWithoutEmailDtoConverter implements Converter<User, UserWithoutEmailDto> {
    @Override
    public UserWithoutEmailDto convert(final User src) {
        return UserWithoutEmailDto.builder()
                .id(src.getId())
                .name(src.getName())
                .build();
    }
}
