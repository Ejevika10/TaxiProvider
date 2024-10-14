package com.modsen.ratingservice.exception;

public record ErrorMessage (
    int errorCode,
    String errorMessage
) {

}