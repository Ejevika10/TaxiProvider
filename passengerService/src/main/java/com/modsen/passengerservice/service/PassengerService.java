package com.modsen.passengerservice.service;

import com.modsen.passengerservice.dto.PageDto;
import com.modsen.passengerservice.dto.PassengerCreateRequestDto;
import com.modsen.passengerservice.dto.PassengerRequestDto;
import com.modsen.passengerservice.dto.PassengerResponseDto;
import com.modsen.passengerservice.dto.UserRatingDto;

import java.util.List;
import java.util.UUID;

public interface PassengerService{
    List<PassengerResponseDto> getAllPassengers();

    PageDto<PassengerResponseDto> getPagePassengers(Integer offset, Integer limit);

    PassengerResponseDto getPassengerById(UUID id);

    PassengerResponseDto getPassengerByEmail(String email);

    PassengerResponseDto addPassenger(PassengerCreateRequestDto requestDTO);

    PassengerResponseDto updatePassenger(UUID id, PassengerRequestDto requestDTO);

    void deletePassenger(UUID id);

    PassengerResponseDto updateRating(UserRatingDto userRatingDTO);
}
