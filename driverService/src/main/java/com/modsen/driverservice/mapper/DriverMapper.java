package com.modsen.driverservice.mapper;

import com.modsen.driverservice.dto.DriverRequestDTO;
import com.modsen.driverservice.dto.DriverResponseDTO;
import com.modsen.driverservice.model.Driver;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DriverMapper {
    Driver toDriver(DriverRequestDTO driverRequestDTO);

    Driver toDriver(DriverResponseDTO driverResponseDTO);

    DriverRequestDTO toDriverRequestDTO(Driver driver);

    DriverResponseDTO toDriverResponseDTO(Driver driver);
}
