package com.modsen.driverservice.mapper;

import com.modsen.driverservice.dto.CarRequestDto;
import com.modsen.driverservice.dto.CarResponseDto;
import com.modsen.driverservice.model.Car;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = CarMapper.class)
public interface CarListMapper {
    List<Car> toCarList(List<CarRequestDto> carRequestDtoList);

    List<CarResponseDto> toCarResponseDTOList(List<Car> carList);
}
