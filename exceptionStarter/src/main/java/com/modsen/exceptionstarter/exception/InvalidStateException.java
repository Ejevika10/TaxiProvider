package com.modsen.exceptionstarter.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class InvalidStateException extends RuntimeException {
    private String message;
}
