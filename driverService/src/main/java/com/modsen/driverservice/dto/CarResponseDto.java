package com.modsen.driverservice.dto;

import lombok.Builder;

@Builder
public record CarResponseDto(
        Long id,

        String color,

        String model,

        String brand,

        String number,

        DriverResponseDto driver) {
}
