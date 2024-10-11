package com.modsen.rideservice.service;

import com.modsen.rideservice.dto.PageDto;
import com.modsen.rideservice.dto.RideRequestDto;
import com.modsen.rideservice.dto.RideResponseDto;

import java.util.List;

public interface RideService{
    List<RideResponseDto> getAllRides();
    PageDto<RideResponseDto> getPageRides(Integer offset, Integer limit);

    List<RideResponseDto> getAllRidesByDriverId(Long driverId);
    PageDto<RideResponseDto> getPageRidesByDriverId(Long driverId, Integer offset, Integer limit);

    List<RideResponseDto> getRidesByPassengerId(Long passengerId);
    PageDto<RideResponseDto> getPageRidesByPassengerId(Long passengerId, Integer offset, Integer limit);

    RideResponseDto getRideById(Long id);
    RideResponseDto createRide(RideRequestDto ride);
    RideResponseDto updateRide(Long id, RideRequestDto ride);
    RideResponseDto setNewState(Long id, String newState);
}
