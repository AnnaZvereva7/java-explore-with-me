package ru.practicum.explore.stats.dto.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CheckDateValidator implements ConstraintValidator<RequestTimeValid, String> {
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void initialize(RequestTimeValid constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String time, ConstraintValidatorContext context) {
        try {
            LocalDateTime requestTime = LocalDateTime.parse(time, FORMATTER);
            return requestTime.isBefore(LocalDateTime.now());
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
