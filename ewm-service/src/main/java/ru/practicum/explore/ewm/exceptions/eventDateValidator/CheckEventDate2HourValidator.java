package ru.practicum.explore.ewm.exceptions.eventDateValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class CheckEventDate2HourValidator implements ConstraintValidator<TwoHoursBeforeEventValid, LocalDateTime> {
    @Override
    public void initialize(TwoHoursBeforeEventValid constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(LocalDateTime eventDate, ConstraintValidatorContext context) {
        if (eventDate == null) return true;
        return eventDate.isAfter(LocalDateTime.now().plusHours(2));
    }
}
