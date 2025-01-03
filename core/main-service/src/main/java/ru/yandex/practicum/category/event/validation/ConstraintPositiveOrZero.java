package ru.yandex.practicum.category.event.validation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(
        validatedBy = {ConstraintPositiveOrZeroValidation.class}
)
public @interface ConstraintPositiveOrZero {
    String message() default "The number must be equal to or greater than zero";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
