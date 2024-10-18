package com.modsen.rideservice.dto;

public record DriverResponseDto(
        Long id,

        String name,

        String email,

        String phone) {
}
