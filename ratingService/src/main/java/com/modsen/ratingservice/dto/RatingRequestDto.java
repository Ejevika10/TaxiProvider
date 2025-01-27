package com.modsen.ratingservice.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record RatingRequestDto (

    @Min(0)
    @NotNull(message = "{rating.ride.mandatory}")
    Long rideId,

    @NotNull(message = "{rating.user.mandatory}")
    String userId,

    @Min(0)
    @Max(5)
    @NotNull(message = "{rating.rating.mandatory}")
    Integer rating,

    String comment){
}
