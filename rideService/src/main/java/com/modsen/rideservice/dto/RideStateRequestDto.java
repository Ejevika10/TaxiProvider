package com.modsen.rideservice.dto;

import jakarta.validation.constraints.NotBlank;

public record RideStateRequestDto (

        @NotBlank(message = "{ride.ridestate.mandatory}")
        String rideState) {
}
