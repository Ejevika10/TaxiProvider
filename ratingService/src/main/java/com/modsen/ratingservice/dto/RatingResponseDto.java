package com.modsen.ratingservice.dto;

public record RatingResponseDto (
        String id,

        Long rideId,

        Long userId,

        Integer rating,

        String comment){
}
