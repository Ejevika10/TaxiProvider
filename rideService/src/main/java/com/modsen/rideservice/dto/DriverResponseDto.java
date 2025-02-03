package com.modsen.rideservice.dto;

import java.util.UUID;

public record DriverResponseDto(
        UUID id,

        String name,

        String email,

        String phone) {
}
