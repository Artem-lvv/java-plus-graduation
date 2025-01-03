package ru.yandex.practicum.category.event.validation;

import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE_USE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {CheckStartAndEndDateValidator.class, CheckStartAndEndDateAdminValidator.class})
public @interface EventStartDateBeforeEndDate {
    String message() default "Event date is null or end date is greater than start date";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};
}
