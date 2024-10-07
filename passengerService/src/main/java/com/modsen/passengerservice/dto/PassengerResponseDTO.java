package com.modsen.passengerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PassengerResponseDTO {

    private Long id;

    private String name;

    private String email;

    private String phone;
}
