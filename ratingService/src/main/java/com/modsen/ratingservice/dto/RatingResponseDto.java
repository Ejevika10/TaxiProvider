package com.modsen.ratingservice.dto;

import lombok.Builder;

@Builder
public record RatingResponseDto (
        String id,

        Long rideId,

        Long userId,

        Integer rating,

        String comment) {
}
