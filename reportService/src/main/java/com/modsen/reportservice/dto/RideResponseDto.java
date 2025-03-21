package com.modsen.reportservice.dto;

import com.modsen.reportservice.model.RideState;

import java.time.LocalDateTime;
import java.util.UUID;

public record RideResponseDto(

        long id,

        UUID driverId,

        UUID passengerId,

        String sourceAddress,

        String destinationAddress,

        RideState rideState,

        LocalDateTime rideDateTime,

        Integer rideCost) {
}
