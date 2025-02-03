package com.modsen.driverservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.io.Serializable;
import java.util.UUID;

@Builder
public record UserRatingDto(
        @JsonProperty("id") UUID id,
        @JsonProperty("rating") double rating)
        implements Serializable {
}
