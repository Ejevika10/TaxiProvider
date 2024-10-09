package com.modsen.passengerservice.mapper;

import com.modsen.passengerservice.dto.PassengerRequestDto;
import com.modsen.passengerservice.dto.PassengerResponseDto;
import com.modsen.passengerservice.model.Passenger;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = PassengerMapper.class)
public interface PassengerListMapper {

    List<PassengerResponseDto> toPassengerResponseDTOList(List<Passenger> passengerList);

    List<Passenger> toPassengerList(List<PassengerRequestDto> passengerRequestDtoList);
}