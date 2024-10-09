package com.modsen.passengerservice.exception;

public record ErrorMessage (
    int errorCode,
    String errorMessage ) {
}