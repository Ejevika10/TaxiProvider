package com.modsen.passengerservice.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record PassengerResponseDto(
    UUID id,

    String name,

    String email,

    String phone,

    Double rating){
}
