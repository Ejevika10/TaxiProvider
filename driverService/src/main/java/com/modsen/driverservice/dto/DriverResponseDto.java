package com.modsen.driverservice.dto;

import lombok.Builder;

import java.io.Serializable;
import java.util.UUID;

@Builder
public record DriverResponseDto(
        UUID id,

        String name,

        String email,

        String phone,

        Double rating) implements Serializable {
}
