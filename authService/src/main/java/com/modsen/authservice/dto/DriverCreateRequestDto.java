package com.modsen.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import static com.modsen.authservice.util.AppConstants.PHONE_REGEXP;

public record DriverCreateRequestDto(
        @NotBlank
        String id,

        @NotBlank(message = "{user.name.mandatory}")
        @Size(min = 4, max = 100)
        String name,

        @NotBlank(message = "{user.email.mandatory}")
        @Email(message = "{user.email.invalid}")
        String email,

        Double rating,

        @NotBlank(message = "{user.phone.mandatory}")
        @Pattern(regexp = PHONE_REGEXP, message = "{user.phone.invalid}")
        String phone ) {
}
