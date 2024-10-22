package com.modsen.rideservice.exception;

import com.modsen.rideservice.util.AppConstants;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Locale;

@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionApiHandler {
    private final MessageSource messageSource;

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage notFoundException(NotFoundException exception) {
        return new ErrorMessage(HttpStatus.NOT_FOUND.value(),
                messageSource.getMessage(exception.getMessage(), new Object[]{}, LocaleContextHolder.getLocale()));
    }

    @ExceptionHandler(DuplicateFieldException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorMessage duplicateFieldException(DuplicateFieldException exception) {
        return new ErrorMessage(HttpStatus.CONFLICT.value(),
                messageSource.getMessage(exception.getMessage(), new Object[]{}, LocaleContextHolder.getLocale()));
    }

    @ExceptionHandler(InvalidStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorMessage invalidStateException(InvalidStateException exception) {
        return new ErrorMessage(HttpStatus.CONFLICT.value(),
                messageSource.getMessage(exception.getMessage(), new Object[]{}, LocaleContextHolder.getLocale()));
    }

    @ExceptionHandler(InvalidFieldValueException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage invalidFieldValueException(InvalidFieldValueException exception) {
        return new ErrorMessage(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ListErrorMessage constraintViolationException(ConstraintViolationException exception) {
        List<String> errors;
        errors = exception.getConstraintViolations().stream().map(error -> error.getPropertyPath().toString() + ": " + error.getMessage()).toList();
        return new ListErrorMessage(HttpStatus.BAD_REQUEST.value(), errors);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ListErrorMessage methodArgumentNotValidException(MethodArgumentNotValidException exception) {
        List<String> errors;
        errors = exception.getBindingResult().getFieldErrors().stream().map(error -> error.getField() + ": " + error.getDefaultMessage()).toList();
        return new ListErrorMessage(HttpStatus.BAD_REQUEST.value(), errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage httpMessageNotReadableException(HttpMessageNotReadableException exception) {
        return new ErrorMessage(HttpStatus.BAD_REQUEST.value(), exception.getMostSpecificCause().getMessage());
    }

    @ExceptionHandler(ClientException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorMessage clientException(ClientException exception) {
        return exception.getErrorMessage();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage defException(Exception exception) {
        return new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                messageSource.getMessage(AppConstants.INTERNAL_SERVER_ERROR, new Object[]{}, Locale.getDefault()));
    }
}
