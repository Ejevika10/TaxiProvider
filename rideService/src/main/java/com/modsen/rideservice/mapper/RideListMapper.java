package com.modsen.rideservice.mapper;

import com.modsen.rideservice.dto.RideRequestDto;
import com.modsen.rideservice.dto.RideResponseDto;
import com.modsen.rideservice.model.Ride;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = RideMapper.class)
public interface RideListMapper {
    List<Ride> toRideList(List<RideRequestDto> rideRequestDtoList);

    List<RideResponseDto> toRideResponseDtoList(List<Ride> rides);
}
