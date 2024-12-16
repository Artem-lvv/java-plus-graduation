package ru.yandex.practicum.validation;

import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE_USE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CheckSearchStatsDateValidator.class)
public @interface CheckSearchStatsDate {
    String message() default "Date is null or end date is greater than start date";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};
}
