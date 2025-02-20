package com.modsen.driverservice.e2e.dto;

import lombok.Builder;

@Builder
public record LoginRequestDto(

        String username,

        String password) {
}
