package com.modsen.passengerservice.dto;

import com.modsen.passengerservice.util.AppConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record PassengerCreateRequestDto(
    @NotBlank(message = "{passenger.id.mandatory}")
    String id,

    @NotBlank(message = "{passenger.name.mandatory}")
    @Size(min = 4, max = 100)
    String name,

    @NotBlank(message = "{passenger.email.mandatory}")
    @Email(message = "{passenger.email.invalid}")
    String email,

    @NotBlank(message = "{passenger.phone.mandatory}")
    @Pattern(regexp = AppConstants.PHONE_REGEXP, message = "{passenger.phone.invalid}")
    String phone,

    Double rating) {
}