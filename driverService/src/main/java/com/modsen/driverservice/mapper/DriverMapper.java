package com.modsen.driverservice.mapper;

import com.modsen.driverservice.dto.DriverRequestDto;
import com.modsen.driverservice.dto.DriverResponseDto;
import com.modsen.driverservice.model.Driver;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DriverMapper {
    @Mapping(target = "rating", defaultValue = "0D")
    @Mapping(target = "deleted", constant = "false")
    Driver toDriver(DriverRequestDto driverRequestDTO);

    Driver toDriver(DriverResponseDto driverResponseDTO);

    DriverRequestDto toDriverRequestDTO(Driver driver);

    DriverResponseDto toDriverResponseDTO(Driver driver);
}
