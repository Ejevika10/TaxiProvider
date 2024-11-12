package com.modsen.rideservice.dto;

import com.modsen.rideservice.model.RideState;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record RideRequestDto(
        @Min(0)
        Long driverId,

        @Min(0) @NotNull(message = "{ride.passenger.mandatory}")
        Long passengerId,

        @NotBlank(message = "{ride.sourceaddress.mandatory}")
        @Size(min = 10, max = 255)
        String sourceAddress,

        @NotBlank(message = "{ride.destinationaddress.mandatory}")
        @Size(min = 10, max = 255)
        String destinationAddress,

        RideState rideState,

        LocalDateTime rideDateTime,

        Integer rideCost) {
}
