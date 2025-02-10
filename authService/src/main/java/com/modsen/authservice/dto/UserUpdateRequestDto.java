package com.modsen.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import static com.modsen.authservice.util.AppConstants.PHONE_REGEXP;
import static com.modsen.authservice.util.AppConstants.UUID_REGEXP;

public record UserUpdateRequestDto(
        @NotBlank(message = "{user.id.required}")
        @Pattern(regexp = UUID_REGEXP, message = "{uuid.invalid}")
        String id,

        @NotBlank(message = "{user.username.required}")
        @Size(min = 4, max = 100)
        String username,

        @NotBlank(message = "{user.email.required}")
        @Email(message = "{user.email.invalid}")
        String email,

        @NotBlank(message = "{user.phone.required}")
        @Pattern(regexp = PHONE_REGEXP, message = "{user.phone.invalid}")
        String phone) {
}
