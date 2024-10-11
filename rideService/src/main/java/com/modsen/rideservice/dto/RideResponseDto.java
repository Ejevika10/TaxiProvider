package com.modsen.rideservice.dto;

import com.modsen.rideservice.model.RideState;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigInteger;
import java.time.LocalDateTime;

public record RideResponseDto (

        @NotNull
        Long id,

        Long driverId,

        @NotNull(message = "")
        Long passengerId,

        @NotBlank(message = "")
        @Size(min = 10, max = 255)
        String sourceAddress,

        @NotBlank(message = "")
        @Size(min = 10, max = 255)
        String destinationAddress,

        @NotBlank(message = "")
        RideState rideState,

        LocalDateTime rideDateTime,

        BigInteger rideCost){
}
