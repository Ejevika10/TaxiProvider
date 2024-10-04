package com.modsen.driverservice.mapper;

import com.modsen.driverservice.dto.CarRequestDTO;
import com.modsen.driverservice.dto.CarResponseDTO;
import com.modsen.driverservice.model.Car;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = CarMapper.class)
public interface CarListMapper {
    List<Car> toCarList(List<CarRequestDTO> carRequestDTOList);

    List<CarResponseDTO> toCarResponseDTOList(List<Car> carList);
}
