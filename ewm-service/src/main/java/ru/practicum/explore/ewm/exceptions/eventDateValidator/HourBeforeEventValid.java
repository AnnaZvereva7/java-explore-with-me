package ru.practicum.explore.ewm.exceptions.eventDateValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(ElementType.FIELD)
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = CheckEventDate1HourValidator.class)
public @interface HourBeforeEventValid {
    String message() default "Must be more then 1 hour before event start";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
