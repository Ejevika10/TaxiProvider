package com.modsen.driverservice.exception;

public record ErrorMessage (
    int errorCode,
    String errorMessage
) {

}