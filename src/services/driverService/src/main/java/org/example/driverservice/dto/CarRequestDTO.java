package org.example.driverservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CarRequestDTO {

    private Long id;

    @NotBlank(message = "Color is mandatory")
    @Size(min = 2, max = 50)
    private String color;

    @NotBlank(message = "Model is mandatory")
    @Size(min = 2, max = 50)
    private String model;

    @NotBlank(message = "Brand is mandatory")
    @Size(min = 2, max = 50)
    private String brand;

    @NotBlank(message = "Number is mandatory")
    @Size(min = 2, max = 20)
    private String number;

    private DriverRequestDTO driver;
}
