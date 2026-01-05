package BiddingSystem.BiddingSystemRepo.customAnotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = PresentOrFutureWithToleranceValidator.class)
public @interface PresentOrFutureWithTolerance {
    String message() default "must be now or in the future";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
