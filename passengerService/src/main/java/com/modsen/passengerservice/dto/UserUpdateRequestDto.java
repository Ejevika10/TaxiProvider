package com.modsen.passengerservice.dto;

public record UserUpdateRequestDto(
        String id,

        String username,

        String email,

        String phone) {
}
