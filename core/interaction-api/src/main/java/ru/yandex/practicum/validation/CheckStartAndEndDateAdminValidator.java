package ru.yandex.practicum.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.ObjectUtils;
import ru.yandex.practicum.event.model.AdminParameter;

public class CheckStartAndEndDateAdminValidator implements ConstraintValidator<EventStartDateBeforeEndDate, AdminParameter> {
    @Override
    public boolean isValid(final AdminParameter adminParameter, final ConstraintValidatorContext context) {
        if (ObjectUtils.isEmpty(adminParameter.getRangeStart()) || ObjectUtils.isEmpty(adminParameter.getRangeEnd())) {
            return true;
        }

        return adminParameter.getRangeStart().isBefore(adminParameter.getRangeEnd());
    }
}
