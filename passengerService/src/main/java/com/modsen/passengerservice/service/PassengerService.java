package com.modsen.passengerservice.service;

import com.modsen.passengerservice.dto.PageDto;
import com.modsen.passengerservice.dto.PassengerRequestDto;
import com.modsen.passengerservice.dto.PassengerResponseDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public interface PassengerService{
    List<PassengerResponseDto> getAllPassengers();

    PageDto<PassengerResponseDto> getPagePassengers(@Min(0) Integer offset, @Min(1) @Max(20) Integer limit);

    PassengerResponseDto getPassengerById(@Min(0) Long id);

    PassengerResponseDto getPassengerByEmail(String email);

    PassengerResponseDto addPassenger(@Valid PassengerRequestDto requestDTO);

    PassengerResponseDto updatePassenger(@Min(0) Long id, @Valid PassengerRequestDto requestDTO);

    void deletePassenger(@Min(0) Long id);
}
