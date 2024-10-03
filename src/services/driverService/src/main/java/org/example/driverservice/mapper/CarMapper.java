package org.example.driverservice.mapper;

import org.example.driverservice.dto.CarRequestDTO;
import org.example.driverservice.dto.CarResponseDTO;
import org.example.driverservice.model.Car;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = DriverMapper.class)
public interface CarMapper {
    Car toCar(CarRequestDTO carRequestDTO);

    Car toCar(CarResponseDTO carResponseDTO);

    CarRequestDTO toCarRequestDTO(Car car);

    CarResponseDTO toCarResponseDTO(Car car);
}
