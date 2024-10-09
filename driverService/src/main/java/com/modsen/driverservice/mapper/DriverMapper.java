package com.modsen.driverservice.mapper;

import com.modsen.driverservice.dto.DriverRequestDto;
import com.modsen.driverservice.dto.DriverResponseDto;
import com.modsen.driverservice.model.Driver;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DriverMapper {
    Driver toDriver(DriverRequestDto driverRequestDTO);

    Driver toDriver(DriverResponseDto driverResponseDTO);

    DriverRequestDto toDriverRequestDTO(Driver driver);

    DriverResponseDto toDriverResponseDTO(Driver driver);
}
