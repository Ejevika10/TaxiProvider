package com.modsen.rideservice.mapper;

import com.modsen.rideservice.dto.RideRequestDto;
import com.modsen.rideservice.dto.RideResponseDto;
import com.modsen.rideservice.model.Ride;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RideMapper {
    RideRequestDto toRideRequestDto(Ride ride);

    Ride toRide(RideRequestDto rideRequestDto);

    Ride toRideDto(RideResponseDto rideResponseDto);

    RideResponseDto toRideResponseDto(Ride ride);
}
