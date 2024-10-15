package com.modsen.driverservice.dto;

public record CarResponseDto(
        Long id,

        String color,

        String model,

        String brand,

        String number,

        DriverResponseDto driver) {
}
