package com.modsen.passengerservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PassengerResponseDto (
    Long id,

    @NotBlank(message = "{passenger.name.mandatory}")
    @Size(min = 2, max = 50)
    String name,

    @NotBlank(message = "{passenger.email.mandatory}")
    @Email(message = "{passenger.email.invalid}")
    String email,

    @NotBlank(message = "{passenger.phone.mandatory}")
    @Pattern(regexp = "^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}$", message = "{passenger.phone.invalid}")
    String phone){
}
