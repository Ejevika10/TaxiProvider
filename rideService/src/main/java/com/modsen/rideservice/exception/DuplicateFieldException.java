package com.modsen.rideservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DuplicateFieldException extends RuntimeException {
    private String message;
}
