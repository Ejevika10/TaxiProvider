package com.modsen.passengerservice.dto;

public record PassengerCreateRequestDto (
    String id,

    String name,

    String email,

    String phone,

    Double rating) {
}