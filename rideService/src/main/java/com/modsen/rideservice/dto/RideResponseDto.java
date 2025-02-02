package com.modsen.rideservice.dto;

import com.modsen.rideservice.model.RideState;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record RideResponseDto(

        Long id,

        UUID driverId,

        UUID passengerId,

        String sourceAddress,

        String destinationAddress,

        RideState rideState,

        LocalDateTime rideDateTime,

        Integer rideCost) {
}
