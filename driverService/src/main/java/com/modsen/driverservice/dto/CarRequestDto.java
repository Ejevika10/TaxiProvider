package com.modsen.driverservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.UUID;

import static com.modsen.driverservice.util.AppConstants.UUID_REGEXP;

@Builder
public record CarRequestDto(
    @NotBlank(message = "{car.color.mandatory}")
    @Size(min = 2, max = 50)
    String color,

    @NotBlank(message = "{car.model.mandatory}")
    @Size(min = 2, max = 50)
    String model,

    @NotBlank(message = "{car.brand.mandatory}")
    @Size(min = 2, max = 50)
    String brand,

    @NotBlank(message = "{car.number.mandatory}")
    @Size(min = 2, max = 20)
    String number,

    @Pattern(regexp = UUID_REGEXP, message = "{uuid.invalid}")
    UUID driverId){

}
