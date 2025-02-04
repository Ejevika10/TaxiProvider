package com.modsen.rideservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import static com.modsen.rideservice.util.AppConstants.UUID_REGEXP;

public record RideAcceptRequestDto(
        @Pattern(regexp = UUID_REGEXP, message = "{uuid.invalid}")
        @NotNull(message = "{ride.driver.mandatory}")
        String driverId) {
}
