package com.modsen.passengerservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class NotFoundException extends RuntimeException {
    private String message;
}
