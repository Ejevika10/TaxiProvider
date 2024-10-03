package org.example.driverservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DriverResponseDTO {
    private Long id;

    @NotBlank(message = "Name is mandatory")
    @Size(min = 4, max = 100)
    private String name;

    @NotBlank(message = "Email is mandatory")
    @Size(min = 4, max = 100)
    private String email;

    @NotBlank(message = "Phone is mandatory")
    @Size(min = 7, max = 20)
    private String phone;
}
