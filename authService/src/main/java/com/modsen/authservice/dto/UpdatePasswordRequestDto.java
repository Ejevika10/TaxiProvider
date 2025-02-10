package com.modsen.authservice.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdatePasswordRequestDto(
        @NotBlank(message = "{user.password.required}")
        String oldPassword,

        @NotBlank(message = "{user.password.required}")
        String newPassword) {
}
