package ru.yandex.practicum.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;
import java.util.Objects;

public class ConstraintFutureInTwoHoursValidation implements ConstraintValidator<ConstraintFutureInTwoHours, LocalDateTime> {
    @Override
    public boolean isValid(LocalDateTime localDateTime, ConstraintValidatorContext constraintValidatorContext) {
        return Objects.isNull(localDateTime) || localDateTime.isAfter(LocalDateTime.now().plusHours(2));
    }
}
