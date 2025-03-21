package com.modsen.reportservice.dto;

import java.util.UUID;

public record DriverResponseDto(
        UUID id,

        String name,

        String email,

        String phone,

        Double rating) {
}
