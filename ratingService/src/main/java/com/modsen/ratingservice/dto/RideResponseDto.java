package com.modsen.ratingservice.dto;

import com.modsen.ratingservice.model.RideState;

import java.time.LocalDateTime;

public record RideResponseDto(

        long id,

        Long driverId,

        long passengerId,

        String sourceAddress,

        String destinationAddress,

        RideState rideState,

        LocalDateTime rideDateTime,

        Integer rideCost) {
}
