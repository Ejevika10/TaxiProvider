package com.modsen.ratingservice.exception;

import java.util.List;

public record ListErrorMessage(
        int errorCode,
        List<String> errorMessages) {
}
