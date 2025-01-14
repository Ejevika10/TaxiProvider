package com.modsen.authservice.dto;

public record PassengerResponseDto(
        long id,

        String name,

        String email,

        String phone,

        Double rating) {
}
