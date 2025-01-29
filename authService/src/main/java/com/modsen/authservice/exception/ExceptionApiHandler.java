package com.modsen.authservice.exception;

import com.modsen.authservice.util.AppConstants;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Locale;

import static com.modsen.authservice.util.AppConstants.USER_DOESNT_EXIST;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionApiHandler {
    private final MessageSource messageSource;

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

    @ExceptionHandler(ServiceUnavailableException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorMessage serviceUnavailableException(ServiceUnavailableException exception) {
        return new ErrorMessage(HttpStatus.SERVICE_UNAVAILABLE.value(),
                messageSource.getMessage(exception.getMessage(), new Object[]{}, Locale.getDefault()));
    }

    @ExceptionHandler(HttpClientErrorException.Unauthorized.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage unauthorizedException(HttpClientErrorException.Unauthorized exception) {
        return new ErrorMessage(HttpStatus.NOT_FOUND.value(),
                messageSource.getMessage(USER_DOESNT_EXIST, new Object[]{}, Locale.getDefault()));
    }

    @ExceptionHandler(KeycloakException.class)
    public ResponseEntity<ErrorMessage> keycloakException(KeycloakException exception) {
        return ResponseEntity.status(exception.getErrorMessage().errorCode())
                .body(exception.getErrorMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage defException(Exception exception) {
        log.info("defException");
        log.info(exception.getMessage());
        log.info(exception.toString());
        return new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                messageSource.getMessage(AppConstants.INTERNAL_SERVER_ERROR, new Object[]{}, Locale.getDefault()));
    }
}
