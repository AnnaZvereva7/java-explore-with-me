package ru.practicum.explore.stats.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.time.format.DateTimeParseException;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String argumentValidationException(MethodArgumentNotValidException e) {
        log.debug("Получен статус 400 Bad request {}", e.getMessage(), e);
        return e.getBindingResult().getFieldError().getDefaultMessage();
    }

    @ExceptionHandler({DateTimeParseException.class, ValidationException.class, ConstraintViolationException.class, WrongPeriodException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String validationException(RuntimeException e) {
        log.debug("Получен статус 400 Bad request {}", e.getMessage(), e);
        return e.getMessage();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String otherException(Exception e) {
        log.debug("Получен статус 500 Internal server error {}", e.getMessage(), e);
        return e.getMessage();
    }
}