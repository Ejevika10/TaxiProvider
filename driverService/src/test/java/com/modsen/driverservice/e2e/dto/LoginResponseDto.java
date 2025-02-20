package com.modsen.driverservice.e2e.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginResponseDto(
        @JsonProperty("access_token")
        String accessToken,

        @JsonProperty("refresh_token")
        String refreshToken,

        @JsonProperty("expires_in")
        String expiresIn,

        @JsonProperty("refresh_expires_in")
        String refreshExpiresIn,

        @JsonProperty("token_type")
        String tokenType,

        @JsonProperty("session_state")
        String sessionState,

        @JsonProperty("scope")
        String scope) {
}
