package com.modsen.driverservice.dto;

public record DriverResponseDto(
        Long id,

        String name,

        String email,

        String phone,

        Double rating) {
}
