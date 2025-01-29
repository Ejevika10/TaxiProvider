package com.modsen.ratingservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.UUID;

public record UserRatingDto(
        @JsonProperty("id") UUID id,
        @JsonProperty("rating") double rating)
        implements Serializable {
}
