package com.modsen.passengerservice.mapper;

import com.modsen.passengerservice.dto.PassengerRequestDto;
import com.modsen.passengerservice.dto.PassengerResponseDto;
import com.modsen.passengerservice.model.Passenger;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PassengerMapper {
    PassengerRequestDto toPassengerRequestDTO(Passenger passenger);

    Passenger toPassenger(PassengerRequestDto requestDTO);

    PassengerResponseDto toPassengerResponseDTO(Passenger passenger);

    Passenger toPassenger(PassengerResponseDto responseDTO);
}
