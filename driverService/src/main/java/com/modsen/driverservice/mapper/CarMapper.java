package com.modsen.driverservice.mapper;

import com.modsen.driverservice.dto.CarRequestDTO;
import com.modsen.driverservice.dto.CarResponseDTO;
import com.modsen.driverservice.model.Car;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = DriverMapper.class)
public interface CarMapper {
    Car toCar(CarRequestDTO carRequestDTO);

    Car toCar(CarResponseDTO carResponseDTO);

    CarRequestDTO toCarRequestDTO(Car car);

    CarResponseDTO toCarResponseDTO(Car car);
}
