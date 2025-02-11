package com.modsen.exceptionstarter.message;

public record ErrorMessage (
    int errorCode,
    String errorMessage
) {

}