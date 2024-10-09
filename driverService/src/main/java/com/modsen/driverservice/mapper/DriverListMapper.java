package com.modsen.driverservice.mapper;

import com.modsen.driverservice.dto.DriverRequestDto;
import com.modsen.driverservice.dto.DriverResponseDto;
import com.modsen.driverservice.model.Driver;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = DriverMapper.class)
public interface DriverListMapper {
    List<Driver> toDriverList(List<DriverRequestDto> driverRequestDtoList);

    List<DriverResponseDto> toDriverResponseDTOList(List<Driver> driverList);
}