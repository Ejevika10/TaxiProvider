package com.modsen.driverservice.mapper;

import com.modsen.driverservice.dto.CarRequestDto;
import com.modsen.driverservice.dto.CarResponseDto;
import com.modsen.driverservice.model.Car;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = DriverMapper.class)
public interface CarMapper {
    Car toCar(CarRequestDto carRequestDTO);

    Car toCar(CarResponseDto carResponseDTO);

    CarRequestDto toCarRequestDTO(Car car);

    CarResponseDto toCarResponseDTO(Car car);
}
