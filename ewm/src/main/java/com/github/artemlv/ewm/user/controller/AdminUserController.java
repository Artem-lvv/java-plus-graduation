package com.github.artemlv.ewm.user.controller;

import com.github.artemlv.ewm.user.model.User;
import com.github.artemlv.ewm.user.model.dto.CreateUserDto;
import com.github.artemlv.ewm.user.model.dto.UserDto;
import com.github.artemlv.ewm.user.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
public class AdminUserController {
    private static final String SIMPLE_NAME = User.class.getSimpleName();
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody final CreateUserDto createUserDto) {
        log.info("Request to create a {} - {}", SIMPLE_NAME, createUserDto);
        return userService.create(createUserDto);
    }

    @GetMapping
    public List<UserDto> getAll(@RequestParam(required = false) final List<Long> ids,
                                @RequestParam(defaultValue = "0") @PositiveOrZero final int from,
                                @RequestParam(defaultValue = "10") @Positive final int size) {
        log.info("Request for all {} beginning - {} size - {}", SIMPLE_NAME, from, size);
        return userService.getAll(ids, from, size);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive final long userId) {
        log.info("Request to delete a {} by id - {}", SIMPLE_NAME, userId);
        userService.deleteById(userId);
    }
}
