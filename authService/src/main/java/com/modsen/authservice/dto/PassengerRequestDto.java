package com.modsen.authservice.dto;

import lombok.Builder;

@Builder
public record PassengerRequestDto(
    String name,

    String email,

    String phone,

    Double rating) {
}
