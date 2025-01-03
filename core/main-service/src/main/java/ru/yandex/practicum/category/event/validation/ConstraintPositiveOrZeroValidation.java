package ru.yandex.practicum.category.event.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

public class ConstraintPositiveOrZeroValidation implements ConstraintValidator<ConstraintPositiveOrZero, Integer> {
    @Override
    public boolean isValid(Integer number, ConstraintValidatorContext constraintValidatorContext) {
        return Objects.isNull(number) || number > 0;
    }
}
