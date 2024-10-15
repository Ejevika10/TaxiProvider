package com.modsen.rideservice.dto;

import com.modsen.rideservice.model.RideState;

import java.time.LocalDateTime;

public record RideResponseDto (

        Long id,

        Long driverId,

        Long passengerId,

        String sourceAddress,

        String destinationAddress,

        RideState rideState,

        LocalDateTime rideDateTime,

        Integer rideCost) {
}
