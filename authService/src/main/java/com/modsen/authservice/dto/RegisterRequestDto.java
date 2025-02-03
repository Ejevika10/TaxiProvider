package com.modsen.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import static com.modsen.authservice.util.AppConstants.PHONE_REGEXP;

@Schema(description = "Register entity")
public record RegisterRequestDto(
        @Schema(description = "Your username", example = "username")
        @NotBlank(message = "{user.username.required}")
        @Size(min = 4, max = 100)
        String username,

        @Schema(description = "Your email", example = "email@gmail.com")
        @NotBlank(message = "{user.email.required}")
        @Email(message = "{user.email.invalid}")
        String email,

        @Schema(description = "Your phone", example = "123456789")
        @NotBlank(message = "{user.phone.required}")
        @Pattern(regexp = PHONE_REGEXP, message = "{user.phone.invalid}")
        String phone,

        @Schema(description = "Your password", example = "password")
        @NotBlank(message = "{user.password.required}")
        String password) {
}
