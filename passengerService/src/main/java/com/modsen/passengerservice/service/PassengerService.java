package com.modsen.passengerservice.service;

import com.modsen.passengerservice.dto.PageDto;
import com.modsen.passengerservice.dto.PassengerRequestDto;
import com.modsen.passengerservice.dto.PassengerResponseDto;
import com.modsen.passengerservice.dto.UserRatingDto;

import java.util.List;

public interface PassengerService{
    List<PassengerResponseDto> getAllPassengers();

    PageDto<PassengerResponseDto> getPagePassengers(Integer offset, Integer limit);

    PassengerResponseDto getPassengerById(Long id);

    PassengerResponseDto getPassengerByEmail(String email);

    PassengerResponseDto addPassenger(PassengerRequestDto requestDTO);

    PassengerResponseDto updatePassenger(Long id, PassengerRequestDto requestDTO);

    void deletePassenger(Long id);

    PassengerResponseDto updateRating(UserRatingDto userRatingDTO);
}
