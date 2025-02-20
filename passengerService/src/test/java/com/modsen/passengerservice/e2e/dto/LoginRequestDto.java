package com.modsen.passengerservice.e2e.dto;

import lombok.Builder;

@Builder
public record LoginRequestDto(

        String username,

        String password) {
}
