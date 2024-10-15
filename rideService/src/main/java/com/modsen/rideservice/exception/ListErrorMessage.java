package com.modsen.rideservice.exception;

import java.util.List;

public record ListErrorMessage(
        int errorCode,
        List<String> errorMessages) {
}
