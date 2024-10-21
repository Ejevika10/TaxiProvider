package com.modsen.rideservice.dto;

public record PassengerResponseDto(
        long id,

        String name,

        String email,

        String phone) {
}
