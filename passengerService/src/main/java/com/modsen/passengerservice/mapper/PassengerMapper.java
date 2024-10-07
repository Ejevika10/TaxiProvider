package com.modsen.passengerservice.mapper;

import com.modsen.passengerservice.dto.PassengerRequestDTO;
import com.modsen.passengerservice.dto.PassengerResponseDTO;
import com.modsen.passengerservice.model.Passenger;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PassengerMapper {
    PassengerRequestDTO toPassengerRequestDTO(Passenger passenger);

    Passenger toPassenger(PassengerRequestDTO requestDTO);

    PassengerResponseDTO toPassengerResponseDTO(Passenger passenger);

    Passenger toPassenger(PassengerResponseDTO responseDTO);
}
