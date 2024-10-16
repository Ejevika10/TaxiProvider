package com.modsen.rideservice.exception;

public record ErrorMessage (
    int errorCode,
    String errorMessage
) {

}