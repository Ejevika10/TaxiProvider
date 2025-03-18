package com.modsen.rideservice.service;

import com.modsen.rideservice.dto.PageDto;
import com.modsen.rideservice.dto.RideAcceptRequestDto;
import com.modsen.rideservice.dto.RideCreateRequestDto;
import com.modsen.rideservice.dto.RideRequestDto;
import com.modsen.rideservice.dto.RideResponseDto;
import com.modsen.rideservice.dto.RideStateRequestDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface RideService {
    List<RideResponseDto> getAllRides();

    PageDto<RideResponseDto> getPageRides(Integer offset, Integer limit);

    List<RideResponseDto> getAllRidesByDriverId(UUID driverId);

    PageDto<RideResponseDto> getPageRidesByDriverId(UUID driverId, Integer offset, Integer limit);

    List<RideResponseDto> getRidesByPassengerId(UUID passengerId);

    PageDto<RideResponseDto> getPageRidesByPassengerId(UUID passengerId, Integer offset, Integer limit);

    List<RideResponseDto> getRidesByDriverIdAndRideDateTime(UUID driverId, LocalDateTime rideDateTime);

    RideResponseDto getRideById(Long id);

    RideResponseDto createRide(RideCreateRequestDto ride, String bearerToken);

    RideResponseDto acceptRide(Long id, RideAcceptRequestDto ride, String bearerToken);

    RideResponseDto cancelRide(Long id, String bearerToken);

    RideResponseDto updateRide(Long id, RideRequestDto ride, String bearerToken);

    RideResponseDto setNewState(Long id, RideStateRequestDto newState);
}
