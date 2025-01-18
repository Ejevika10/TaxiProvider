package com.modsen.authservice.dto;

public record DriverCreateRequestDto(
    String id,

    String name,

    String email,

    Double rating,

    String phone ) {
}
