package com.modsen.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import static com.modsen.authservice.util.AppConstants.UUID_REGEXP;

public record UserDeleteRequestDto(
        @NotBlank(message = "{user.id.required}")
        @Pattern(regexp = UUID_REGEXP, message = "{uuid.invalid}")
        String id) {
}
