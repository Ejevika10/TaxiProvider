package com.modsen.passengerservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class InvalidFieldValueException extends RuntimeException {
    private String message;
}
