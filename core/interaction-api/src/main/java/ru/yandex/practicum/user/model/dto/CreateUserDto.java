package ru.yandex.practicum.user.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserDto(
        @Size(min = 6, max = 254)
        @NotBlank
        @Email
        String email,
        @Size(min = 2, max = 250)
        @NotBlank
        String name
) {
}
