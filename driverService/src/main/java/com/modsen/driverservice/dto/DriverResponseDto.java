package com.modsen.driverservice.dto;

import lombok.Builder;

@Builder
public record DriverResponseDto(
        Long id,

        String name,

        String email,

        String phone,

        Double rating) {
}
