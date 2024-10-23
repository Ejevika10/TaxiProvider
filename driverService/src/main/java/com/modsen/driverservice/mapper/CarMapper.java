package com.modsen.driverservice.mapper;

import com.modsen.driverservice.dto.CarRequestDto;
import com.modsen.driverservice.dto.CarResponseDto;
import com.modsen.driverservice.model.Car;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = DriverMapper.class)
public interface CarMapper {
    Car toCar(CarRequestDto carRequestDTO);

    Car toCar(CarResponseDto carResponseDTO);

    CarRequestDto toCarRequestDTO(Car car);

    CarResponseDto toCarResponseDTO(Car car);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCar(@MappingTarget Car car, CarRequestDto carRequestDto);
}
