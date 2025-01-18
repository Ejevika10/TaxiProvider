package com.modsen.passengerservice.dto;

import lombok.Builder;

@Builder
public record PassengerResponseDto (
    Long id,

    String name,

    String email,

    String phone,

    Double rating){
}
