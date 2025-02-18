package com.modsen.driverservice.e2e.dto;

public record LoginResponseDto(
    String access_token,
    String refresh_token,
    String expires_in,
    String refresh_expires_in,
    String token_type,
    String session_state,
    String scope) {
}
