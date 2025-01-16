package ru.yandex.practicum.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.ObjectUtils;
import ru.yandex.practicum.event.model.PublicParameter;

public class CheckStartAndEndDateValidator implements ConstraintValidator<EventStartDateBeforeEndDate, PublicParameter> {
    @Override
    public boolean isValid(final PublicParameter publicParameter, final ConstraintValidatorContext context) {
        if (ObjectUtils.isEmpty(publicParameter.getRangeStart()) || ObjectUtils.isEmpty(publicParameter.getRangeEnd())) {
            return true;
        }

        return publicParameter.getRangeStart().isBefore(publicParameter.getRangeEnd());
    }
}
