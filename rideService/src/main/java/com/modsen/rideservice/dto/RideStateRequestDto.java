package com.modsen.rideservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record RideStateRequestDto (

        @NotBlank(message = "{ride.ridestate.mandatory}")
        String rideState) {
}
