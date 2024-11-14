package com.modsen.passengerservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.io.Serializable;

@Builder
public record UserRatingDto(
        @JsonProperty("id") long id,
        @JsonProperty("rating") double rating)
        implements Serializable {
}
