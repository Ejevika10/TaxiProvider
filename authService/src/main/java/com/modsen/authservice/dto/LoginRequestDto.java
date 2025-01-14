package com.modsen.authservice.dto;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(

    @NotBlank(message = "{user.username.required}")
    String username,

    @NotBlank(message = "{user.password.required}")
    String password ) {
}
