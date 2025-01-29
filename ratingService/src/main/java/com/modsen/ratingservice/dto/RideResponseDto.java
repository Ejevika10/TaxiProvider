package com.modsen.ratingservice.dto;

import com.modsen.ratingservice.model.RideState;

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
