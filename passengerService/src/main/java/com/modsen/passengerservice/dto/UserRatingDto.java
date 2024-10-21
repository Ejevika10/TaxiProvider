package com.modsen.passengerservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public record UserRatingDto(
        @JsonProperty("id") long id,
        @JsonProperty("rating") double rating)
        implements Serializable {
}
