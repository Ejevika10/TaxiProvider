package com.modsen.driverservice.dto;

import jakarta.validation.constraints.*;

public record DriverResponseDto (
    @NotNull
    Long id,

    @NotBlank(message = "{driver.name.mandatory}")
    @Size(min = 4, max = 100)
    String name,

    @NotBlank(message = "{driver.email.mandatory}")
    @Email(message = "{driver.email.invalid}")
    String email,

    @NotBlank(message = "{driver.phone.mandatory}")
    @Pattern(regexp = "^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}$", message = "{driver.phone.invalid}")
    String phone ) {
}
