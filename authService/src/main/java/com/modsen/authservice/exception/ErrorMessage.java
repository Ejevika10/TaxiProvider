package com.modsen.authservice.exception;

public record ErrorMessage (
    int errorCode,
    String errorMessage
) {

}