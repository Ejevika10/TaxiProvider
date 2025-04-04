package com.modsen.authservice.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record DriverResponseDto(
        UUID id,

        String name,

        String email,

        String phone,

        Double rating) {
}
