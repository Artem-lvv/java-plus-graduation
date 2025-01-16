package ru.yandex.practicum.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

public class ConstraintNotZeroValidation implements ConstraintValidator<ConstraintNotZero, Double> {
    @Override
    public boolean isValid(Double number, ConstraintValidatorContext constraintValidatorContext) {
        return Objects.nonNull(number) && number != 0;
    }
}
