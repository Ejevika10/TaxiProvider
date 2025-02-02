package com.modsen.driverservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import static com.modsen.driverservice.util.AppConstants.PHONE_REGEXP;

public record DriverCreateRequestDto(
    @NotBlank
    String id,

    @NotBlank(message = "{driver.name.mandatory}")
    @Size(min = 4, max = 100)
    String name,

    @NotBlank(message = "{driver.email.mandatory}")
    @Email(message = "{driver.email.invalid}")
    String email,

    Double rating,

    @NotBlank(message = "{driver.phone.mandatory}")
    @Pattern(regexp = PHONE_REGEXP, message = "{driver.phone.invalid}")
    String phone) {
}
