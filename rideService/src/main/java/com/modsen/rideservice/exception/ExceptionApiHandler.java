package com.modsen.rideservice.exception;

import com.modsen.rideservice.util.AppConstants;
import feign.RetryableException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Locale;

@Slf4j
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
        errors = exception.getBindingResult().getFieldErrors().stream().map(error -> error.getField() + ": " + error.getDefaultMessage()).toList();
        return new ListErrorMessage(HttpStatus.BAD_REQUEST.value(), errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage httpMessageNotReadableException(HttpMessageNotReadableException exception) {
        return new ErrorMessage(HttpStatus.BAD_REQUEST.value(), exception.getMostSpecificCause().getMessage());
    }

    @ExceptionHandler(ClientException.class)
    public ResponseEntity<ErrorMessage> clientException(ClientException exception) {
        return ResponseEntity.status(exception.getErrorMessage().errorCode())
                .body(exception.getErrorMessage());
    }

    //thrown when circuit breaker is open
    @ExceptionHandler(CallNotPermittedException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorMessage circuitBreakerException(CallNotPermittedException exception) {
        return new ErrorMessage(HttpStatus.SERVICE_UNAVAILABLE.value(),
                messageSource.getMessage(AppConstants.SERVICE_UNAVAILABLE,
                        new Object[]{}, Locale.getDefault()));
    }

    //thrown when circuit breaker is closed and feign client is trying to send request to non-existent service
    @ExceptionHandler(RetryableException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorMessage serviceInstanceWasntResolvedException(RetryableException exception) {
        return new ErrorMessage(HttpStatus.SERVICE_UNAVAILABLE.value(),
                messageSource.getMessage(AppConstants.SERVICE_UNAVAILABLE,
                        new Object[]{}, Locale.getDefault()));
    }

    //any unknown exception from feign client
    @ExceptionHandler(ServiceUnavailableException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorMessage serviceUnavailableException(ServiceUnavailableException exception) {
        return new ErrorMessage(HttpStatus.SERVICE_UNAVAILABLE.value(),
                messageSource.getMessage(exception.getMessage(), new Object[]{}, Locale.getDefault()));
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
        log.info(exception.getMessage());
        return new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                messageSource.getMessage(AppConstants.INTERNAL_SERVER_ERROR, new Object[]{}, Locale.getDefault()));
    }
}
