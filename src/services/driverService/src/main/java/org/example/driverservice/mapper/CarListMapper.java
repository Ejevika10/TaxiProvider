package org.example.driverservice.mapper;

import org.example.driverservice.dto.CarRequestDTO;
import org.example.driverservice.dto.CarResponseDTO;
import org.example.driverservice.model.Car;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = CarMapper.class)
public interface CarListMapper {
    List<Car> toCarList(List<CarRequestDTO> carRequestDTOList);

    List<CarResponseDTO> toCarResponseDTOList(List<Car> carList);
}
