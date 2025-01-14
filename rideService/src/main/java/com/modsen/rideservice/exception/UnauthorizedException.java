package com.modsen.rideservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UnauthorizedException extends RuntimeException {
    private String message;
}
