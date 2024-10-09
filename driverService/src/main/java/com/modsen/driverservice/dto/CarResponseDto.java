package com.modsen.driverservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CarResponseDto (
    @NotNull
    Long id,

    @NotBlank(message = "car.color.mandatory")
    @Size(min = 2, max = 50)
    String color,

    @NotBlank(message = "car.model.mandatory")
    @Size(min = 2, max = 50)
    String model,

    @NotBlank(message = "car.brand.mandatory")
    @Size(min = 2, max = 50)
    String brand,

    @NotBlank(message = "car.number.mandatory")
    @Size(min = 2, max = 20)
    String number,

    DriverResponseDto driver) {
}
