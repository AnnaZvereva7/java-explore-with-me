package ru.practicum.explore.ewm.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto methodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.debug("Получен статус 400 Bad request {}", e.getMessage(), e);
        ExceptionDto dto = new ExceptionDto();
        dto.setStatus("BAD_REQUEST");
        dto.setReason("Incorrectly made request.");
        dto.setMessage(e.getBindingResult().getFieldError().getDefaultMessage());
        dto.setTimestamp(LocalDateTime.now());
        return dto;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto wrongRequestParam(MissingServletRequestParameterException e) {
        log.debug("Получен статус 400 Bad request {}", e.getMessage(), e);
        ExceptionDto dto = new ExceptionDto();
        dto.setStatus("BAD_REQUEST");
        dto.setReason("Incorrectly made request.");
        dto.setMessage(e.getMessage());
        dto.setTimestamp(LocalDateTime.now());
        return dto;
    }

    @ExceptionHandler({WrongStatusException.class, WrongParamException.class, EventDateException.class, NumberFormatException.class, ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto wrongRequestParametr(RuntimeException e) {
        log.debug("Получен статус 400 Bad request {}", e.getMessage(), e);
        ExceptionDto dto = new ExceptionDto();
        dto.setStatus("BAD_REQUEST");
        dto.setReason("Incorrectly made request.");
        dto.setMessage(e.getMessage());
        dto.setTimestamp(LocalDateTime.now());
        return dto;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDto notFoundException(NotFoundException e) {
        log.debug("Получен статус 404 Not found {}", e.getMessage(), e);
        ExceptionDto dto = new ExceptionDto();
        dto.setStatus("NOT_FOUND");
        dto.setReason("The required object was not found.");
        dto.setMessage(e.getMessage());
        dto.setTimestamp(LocalDateTime.now());
        return dto;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionDto sqlException(DataIntegrityViolationException e) {
        log.debug("Получен статус 409 Conflict {}", e.getMessage(), e);
        ExceptionDto dto = new ExceptionDto();
        dto.setStatus("CONFLICT");
        dto.setReason("Integrity constraint has been violated.");
        dto.setMessage(e.getMessage());
        dto.setTimestamp(LocalDateTime.now());
        return dto;
    }

    @ExceptionHandler({AccessException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionDto fobbidenException(RuntimeException e) {
        log.debug("Получен статус 409 Conflict {}", e.getMessage(), e);
        ExceptionDto dto = new ExceptionDto();
        dto.setStatus("FORBIDDEN");
        dto.setReason("For the requested operation the conditions are not met.");
        dto.setMessage(e.getMessage());
        dto.setTimestamp(LocalDateTime.now());
        return dto;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> otherException(Exception e) {
        log.debug("Получен статус 500 Internal server error {}", e.getMessage(), e);
        return Map.of("error", e.getClass().toString());
    }
}
