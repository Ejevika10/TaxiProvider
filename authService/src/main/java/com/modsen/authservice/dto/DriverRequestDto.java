package com.modsen.authservice.dto;

import lombok.Builder;

@Builder
public record DriverRequestDto(
    String name,

    String email,

    Double rating,

    String phone ) {
}
