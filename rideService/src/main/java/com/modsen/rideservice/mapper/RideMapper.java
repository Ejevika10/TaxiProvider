package com.modsen.rideservice.mapper;

import com.modsen.rideservice.dto.RideCreateRequestDto;
import com.modsen.rideservice.dto.RideRequestDto;
import com.modsen.rideservice.dto.RideResponseDto;
import com.modsen.rideservice.model.Ride;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RideMapper {
    Ride toRide(RideRequestDto rideRequestDto);

    Ride toRide(RideCreateRequestDto rideRequestDto);

    RideResponseDto toRideResponseDto(Ride ride);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateRide(@MappingTarget Ride ride, RideRequestDto rideRequestDto);
}
