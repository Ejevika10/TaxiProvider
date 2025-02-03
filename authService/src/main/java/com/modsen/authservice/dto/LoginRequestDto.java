package com.modsen.authservice.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Login entity")
public record LoginRequestDto(

        @Schema(description = "Your username", example = "username")
        @NotBlank(message = "{user.username.required}")
        String username,

        @Schema(description = "Your password", example = "password")
        @NotBlank(message = "{user.password.required}")
        String password) {
}
