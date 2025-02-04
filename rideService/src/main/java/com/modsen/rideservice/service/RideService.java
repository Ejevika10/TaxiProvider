package com.modsen.rideservice.service;

import com.modsen.rideservice.dto.PageDto;
import com.modsen.rideservice.dto.RideAcceptRequestDto;
import com.modsen.rideservice.dto.RideCreateRequestDto;
import com.modsen.rideservice.dto.RideRequestDto;
import com.modsen.rideservice.dto.RideResponseDto;
import com.modsen.rideservice.dto.RideStateRequestDto;

import java.util.List;
import java.util.UUID;

public interface RideService {
    List<RideResponseDto> getAllRides();

    PageDto<RideResponseDto> getPageRides(Integer offset, Integer limit);

    List<RideResponseDto> getAllRidesByDriverId(UUID driverId);

    PageDto<RideResponseDto> getPageRidesByDriverId(UUID driverId, Integer offset, Integer limit);

    List<RideResponseDto> getRidesByPassengerId(UUID passengerId);

    PageDto<RideResponseDto> getPageRidesByPassengerId(UUID passengerId, Integer offset, Integer limit);

    RideResponseDto getRideById(Long id);

    RideResponseDto createRide(RideCreateRequestDto ride, String authorizationToken);

    RideResponseDto acceptRide(Long id, RideAcceptRequestDto ride, String authorizationToken);

    RideResponseDto cancelRide(Long id, String authorizationToken);

    RideResponseDto updateRide(Long id, RideRequestDto ride, String authorizationToken);

    RideResponseDto setNewState(Long id, RideStateRequestDto newState);
}
