package com.modsen.passengerservice.dto;

import com.modsen.passengerservice.configuration.MessageConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PassengerRequestDto (
    @NotBlank(message = "{passenger.name.mandatory}")
    @Size(min = 2, max = 50)
    String name,

    @NotBlank(message = "{passenger.email.mandatory}")
    @Email(message = "{passenger.email.invalid}")
    String email,

    @NotBlank(message = "{passenger.phone.mandatory}")
    @Pattern(regexp = MessageConstants.PHONE_REGEXP, message = "{passenger.phone.invalid}")
    String phone) {
}
