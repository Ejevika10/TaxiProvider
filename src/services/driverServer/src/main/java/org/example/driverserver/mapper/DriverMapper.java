package org.example.driverserver.mapper;

import org.example.driverserver.dto.DriverRequestDTO;
import org.example.driverserver.dto.DriverResponseDTO;
import org.example.driverserver.model.Driver;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DriverMapper {
    Driver toDriver(DriverRequestDTO driverRequestDTO);
    Driver toDriver(DriverResponseDTO driverResponseDTO);
    DriverRequestDTO toDriverRequestDTO(Driver driver);
    DriverResponseDTO toDriverResponseDTO(Driver driver);
}
