package ru.yandex.practicum.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.ObjectUtils;
import ru.yandex.practicum.SearchStats;

public class CheckSearchStatsDateValidator implements ConstraintValidator<CheckSearchStatsDate, SearchStats> {
    @Override
    public boolean isValid(final SearchStats searchStats, final ConstraintValidatorContext context) {
        return !ObjectUtils.isEmpty(searchStats.getStart())
                && !ObjectUtils.isEmpty(searchStats.getEnd())
                && searchStats.getStart().isBefore(searchStats.getEnd());
    }
}
