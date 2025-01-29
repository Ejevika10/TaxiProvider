package com.modsen.passengerservice.exception;

import com.modsen.passengerservice.util.AppConstants;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

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

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ListErrorMessage constraintViolationException(ConstraintViolationException exception) {
        List<String> errors;
        errors = exception.getConstraintViolations().stream()
                .map(error -> {
                    String fieldPath = error.getPropertyPath().toString();
                    String[] fieldParts = fieldPath.split("\\.");
                    return fieldParts[fieldParts.length - 1] + ": " + error.getMessage();})
                .toList();
        return new ListErrorMessage(HttpStatus.BAD_REQUEST.value(), errors);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ListErrorMessage methodArgumentNotValidException(MethodArgumentNotValidException exception) {
        List<String> errors;
        errors = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();
        return new ListErrorMessage(HttpStatus.BAD_REQUEST.value(), errors);
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorMessage forbiddenException(ForbiddenException exception) {
        return new ErrorMessage(HttpStatus.FORBIDDEN.value(),
                messageSource.getMessage(exception.getMessage(), new Object[]{}, LocaleContextHolder.getLocale()));
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorMessage unauthorizedException(UnauthorizedException exception) {
        return new ErrorMessage(HttpStatus.UNAUTHORIZED.value(),
                messageSource.getMessage(exception.getMessage(), new Object[]{}, LocaleContextHolder.getLocale()));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage defException(Exception exception) {
        return new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                messageSource.getMessage(AppConstants.INTERNAL_SERVER_ERROR, new Object[]{}, LocaleContextHolder.getLocale()));
    }
}
