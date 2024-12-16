package com.github.artemlv.ewm.user.service;

import com.github.artemlv.ewm.user.model.User;
import com.github.artemlv.ewm.user.model.dto.CreateUserDto;
import com.github.artemlv.ewm.user.model.dto.UserDto;
import com.github.artemlv.ewm.user.storage.UserStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Qualifier("mvcConversionService")
    private final ConversionService cs;
    private final UserStorage userStorage;

    @Override
    public UserDto create(CreateUserDto createUserDto) {
        User user = cs.convert(createUserDto, User.class);
        return cs.convert(userStorage.save(user), UserDto.class);
    }

    @Override
    public List<UserDto> getAll(final List<Long> ids, final int from, final int size) {
        return userStorage.getAll(ids, from, size).stream()
                .map(user -> cs.convert(user, UserDto.class))
                .toList();
    }

    @Override
    public UserDto getById(final long id) {
        return cs.convert(userStorage.getByIdOrElseThrow(id), UserDto.class);
    }

    @Override
    public void deleteById(final long id) {
        userStorage.existsByIdOrElseThrow(id);
        userStorage.deleteById(id);
    }
}
