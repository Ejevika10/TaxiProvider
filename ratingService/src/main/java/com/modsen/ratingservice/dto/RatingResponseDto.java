package com.modsen.ratingservice.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record RatingResponseDto(
        String id,

        Long rideId,

        UUID userId,

        Integer rating,

        String comment) {
}
