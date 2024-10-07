package com.modsen.passengerservice.service;

import com.modsen.passengerservice.dto.PageDTO;
import com.modsen.passengerservice.dto.PassengerRequestDTO;
import com.modsen.passengerservice.dto.PassengerResponseDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public interface PassengerService{
    List<PassengerResponseDTO> getAllPassengers();

    PageDTO<PassengerResponseDTO> getPagePassengers(@Min(0) Integer offset, @Min(1) @Max(20) Integer limit);

    PassengerResponseDTO getPassengerById(@Min(0) Long id);

    PassengerResponseDTO getPassengerByEmail(String email);

    PassengerResponseDTO addPassenger(@Valid PassengerRequestDTO requestDTO);

    PassengerResponseDTO updatePassenger(@Valid PassengerRequestDTO requestDTO);

    void deletePassenger(@Min(0) Long id);
}
