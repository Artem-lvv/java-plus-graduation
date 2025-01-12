package ru.yandex.practicum;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.user.model.dto.CreateUserDto;
import ru.yandex.practicum.user.model.dto.UserDto;

import java.util.List;

@FeignClient(name = "${admin.user.service.name:USER-SERVICE}",
        url = "${admin.user.service.url}"
//        ,
//        configuration = FeignOkHttpConfig.class
)
public interface AdminUserClient {

    @PostMapping("/admin/users")
    UserDto create(@RequestBody @Valid CreateUserDto createUserDto);

    @GetMapping("/admin/users")
    List<UserDto> getAll(@RequestParam(required = false) List<Long> ids,
                         @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                         @RequestParam(defaultValue = "10") @Positive int size);

    @DeleteMapping("/admin/users/{userId}")
    void delete(@PathVariable @Positive long userId);
}
