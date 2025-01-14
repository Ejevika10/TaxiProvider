package com.modsen.authservice.dto;

import lombok.Builder;

@Builder
public record DriverResponseDto(
        Long id,

        String name,

        String email,

        String phone,

        Double rating) {
}
