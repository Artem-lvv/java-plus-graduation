package ru.yandex.practicum.category.user.converter;

import ru.yandex.practicum.category.user.model.User;
import ru.yandex.practicum.category.user.model.dto.UserWithoutEmailDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

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
