package org.example.driverserver.mapper;

import org.example.driverserver.dto.CarRequestDTO;
import org.example.driverserver.dto.CarResponseDTO;
import org.example.driverserver.model.Car;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = DriverMapper.class)
public interface CarMapper {
    Car toCar(CarRequestDTO carRequestDTO);

    Car toCar(CarResponseDTO carResponseDTO);

    CarRequestDTO toCarRequestDTO(Car car);

    CarResponseDTO toCarResponseDTO(Car car);
}
