package com.modsen.rideservice.dto;

public record DriverResponseDto(
        long id,

        String name,

        String email,

        String phone) {
}
