package com.modsen.exceptionstarter.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class InvalidFieldValueException extends RuntimeException {
    private String message;
}
