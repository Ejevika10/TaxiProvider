package com.modsen.rideservice.dto;

import com.modsen.rideservice.model.RideState;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDateTime;

import static com.modsen.rideservice.util.AppConstants.UUID_REGEXP;

@Builder
public record RideRequestDto(
        @Pattern(regexp = UUID_REGEXP, message = "{uuid.invalid}")
        String driverId,

        @NotNull(message = "{ride.passenger.mandatory}")
        @Pattern(regexp = UUID_REGEXP, message = "{uuid.invalid}")
        String passengerId,

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
