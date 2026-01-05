package BiddingSystem.BiddingSystemRepo.customAnotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Duration;
import java.time.ZonedDateTime;

public class PresentOrFutureWithToleranceValidator
        implements ConstraintValidator<PresentOrFutureWithTolerance, ZonedDateTime> {

    private static final Duration GRACE = Duration.ofSeconds(2);

    @Override
    public boolean isValid(ZonedDateTime value, ConstraintValidatorContext context) {
        if (value == null) return true;

        return !value.isBefore(ZonedDateTime.now().minus(GRACE));
    }
}
