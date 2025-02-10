package com.modsen.driverservice.dto;

public record UserUpdateRequestDto(
        String id,

        String username,

        String email,

        String phone) {
}
