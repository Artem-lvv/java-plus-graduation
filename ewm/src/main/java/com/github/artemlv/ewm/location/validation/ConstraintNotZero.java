package com.github.artemlv.ewm.location.validation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(
        validatedBy = {ConstraintNotZeroValidation.class}
)
public @interface ConstraintNotZero {
    String message() default "The number must be not zero or null";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
