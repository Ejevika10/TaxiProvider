package com.modsen.passengerservice.mapper;

import com.modsen.passengerservice.dto.PassengerRequestDTO;
import com.modsen.passengerservice.dto.PassengerResponseDTO;
import com.modsen.passengerservice.model.Passenger;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = PassengerMapper.class)
public interface PassengerListMapper {

    List<PassengerResponseDTO> toPassengerResponseDTOList(List<Passenger> passengerList);

    List<Passenger> toPassengerList(List<PassengerRequestDTO> passengerRequestDTOList);
}