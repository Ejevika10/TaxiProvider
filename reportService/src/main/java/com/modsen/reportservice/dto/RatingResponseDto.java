package com.modsen.reportservice.dto;

import lombok.Builder;

import java.io.Serializable;
import java.util.UUID;

@Builder
public record RatingResponseDto(
        String id,

        Long rideId,

        UUID userId,

        Integer rating,

        String comment) implements Serializable {
}
