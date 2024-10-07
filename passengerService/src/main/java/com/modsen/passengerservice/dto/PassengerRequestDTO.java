package com.modsen.passengerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PassengerRequestDTO {

    private Long id;

    @NotBlank(message = "Name is mandatory")
    @Size(min = 2, max = 50)
    private String name;

    @NotBlank(message = "Email is mandatory")
    @Size(min = 5, max = 50)
    private String email;

    @NotBlank(message = "Phone is mandatory")
    @Size(min = 5, max = 50)
    private String phone;
}
