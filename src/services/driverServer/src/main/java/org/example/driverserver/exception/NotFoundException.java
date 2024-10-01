package org.example.driverserver.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class NotFoundException extends RuntimeException{
    private String message;
    private Long errorCode;
}
