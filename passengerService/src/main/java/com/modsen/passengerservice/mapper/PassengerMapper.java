package com.modsen.passengerservice.mapper;

import com.modsen.passengerservice.dto.PassengerRequestDto;
import com.modsen.passengerservice.dto.PassengerResponseDto;
import com.modsen.passengerservice.model.Passenger;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PassengerMapper {
    @Mapping(target = "rating", defaultValue = "0D")
    @Mapping(target = "deleted", constant = "false")
    Passenger toPassenger(PassengerRequestDto requestDTO);

    PassengerResponseDto toPassengerResponseDTO(Passenger passenger);

    Passenger toPassenger(PassengerResponseDto responseDTO);

    PassengerRequestDto toPassengerRequestDTO(Passenger passenger);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePassenger(@MappingTarget Passenger passenger, PassengerRequestDto passengerRequestDto);
}
