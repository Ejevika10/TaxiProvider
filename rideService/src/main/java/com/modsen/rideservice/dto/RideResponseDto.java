package com.modsen.rideservice.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
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

        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        LocalDateTime rideDateTime,

        Integer rideCost) {
}
