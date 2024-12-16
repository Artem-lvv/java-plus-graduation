package com.github.artemlv.ewm.user.converter;

import com.github.artemlv.ewm.user.model.User;
import com.github.artemlv.ewm.user.model.dto.UserWithoutEmailDto;
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
