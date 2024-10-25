package com.modsen.rideservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ServiceUnavailableException extends RuntimeException {
    private String message;
}
