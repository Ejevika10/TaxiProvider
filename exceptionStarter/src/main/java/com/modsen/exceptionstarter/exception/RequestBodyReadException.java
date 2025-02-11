package com.modsen.exceptionstarter.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RequestBodyReadException extends RuntimeException {
    private String message;
}
