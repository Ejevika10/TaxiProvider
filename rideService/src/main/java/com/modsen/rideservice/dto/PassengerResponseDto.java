package com.modsen.rideservice.dto;

import java.util.UUID;

public record PassengerResponseDto(
        UUID id,

        String name,

        String email,

        String phone) {
}
