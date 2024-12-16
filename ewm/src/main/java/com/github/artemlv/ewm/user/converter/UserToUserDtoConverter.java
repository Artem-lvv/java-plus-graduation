package com.github.artemlv.ewm.user.converter;

import com.github.artemlv.ewm.user.model.User;
import com.github.artemlv.ewm.user.model.dto.UserDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserToUserDtoConverter implements Converter<User, UserDto> {
    @Override
    public UserDto convert(final User src) {
        return UserDto.builder()
                .id(src.getId())
                .email(src.getEmail())
                .name(src.getName())
                .build();
    }
}
