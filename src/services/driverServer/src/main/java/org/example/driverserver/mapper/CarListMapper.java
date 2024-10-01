package org.example.driverserver.mapper;

import org.example.driverserver.dto.CarRequestDTO;
import org.example.driverserver.dto.CarResponseDTO;
import org.example.driverserver.model.Car;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = CarMapper.class)
public interface CarListMapper {
    List<Car> toCarList(List<CarRequestDTO> carRequestDTOList);

    List<CarResponseDTO> toCarResponseDTOList(List<Car> carList);
}
