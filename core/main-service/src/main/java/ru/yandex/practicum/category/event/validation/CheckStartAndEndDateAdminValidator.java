package ru.yandex.practicum.category.event.validation;

import ru.yandex.practicum.category.event.model.AdminParameter;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.ObjectUtils;

public class CheckStartAndEndDateAdminValidator implements ConstraintValidator<EventStartDateBeforeEndDate, AdminParameter> {
    @Override
    public boolean isValid(final AdminParameter adminParameter, final ConstraintValidatorContext context) {
        if (ObjectUtils.isEmpty(adminParameter.getRangeStart()) || ObjectUtils.isEmpty(adminParameter.getRangeEnd())) {
            return true;
        }

        return adminParameter.getRangeStart().isBefore(adminParameter.getRangeEnd());
    }
}
