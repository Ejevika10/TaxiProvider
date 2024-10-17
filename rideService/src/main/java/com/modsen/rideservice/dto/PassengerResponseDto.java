package com.modsen.rideservice.dto;

public record PassengerResponseDto(
        Long id,

        String name,

        String email,

        String phone) {
}
