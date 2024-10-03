package org.example.driverservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DuplicateFieldException extends RuntimeException {
    private String message;
    private Long errorCode;
}
