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
        validatedBy = {ConstraintFutureInTwoHoursValidation.class}
)
public @interface ConstraintFutureInTwoHours {
    String message() default "The date and time when the event is scheduled cannot be earlier than two hours "
            + "from the current moment";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
