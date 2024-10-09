package com.modsen.driverservice.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;

@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionApiHandler {
    private final MessageSource messageSource;

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage notFoundException(NotFoundException exception) {
        return new ErrorMessage(404, exception.getMessage());
    }

    @ExceptionHandler(DuplicateFieldException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage duplicateFieldException(DuplicateFieldException exception) {
        return new ErrorMessage(400, exception.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage constraintViolationException(ConstraintViolationException exception) {
        return new ErrorMessage(400, exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage defException(Exception exception) {
        return new ErrorMessage(500, messageSource.getMessage("internal.server.error", new Object[]{}, Locale.getDefault()));
        //return new ErrorMessage(500, exception.getMessage());
    }
}
