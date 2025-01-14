package com.modsen.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import static com.modsen.authservice.util.AppConstants.PHONE_REGEXP;

public record RegisterRequestDto (
        @NotBlank(message = "{user.username.required}")
        String username,

        @NotBlank(message = "{user.email.required}")
        @Email(message = "{user.email.invalid}")
        String email,

        @NotBlank(message = "{user.phone.required}")
        @Pattern(regexp = PHONE_REGEXP, message = "{user.phone.invalid}")
        String phone,

        @NotBlank(message = "{user.password.required}")
        String password,

        String userRole
) {
}
