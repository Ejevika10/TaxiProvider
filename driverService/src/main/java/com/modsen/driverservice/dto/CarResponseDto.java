package com.modsen.driverservice.dto;

import lombok.Builder;

import java.io.Serializable;

@Builder
public record CarResponseDto(
        Long id,

        String color,

        String model,

        String brand,

        String number,

        DriverResponseDto driver) implements Serializable {
}
