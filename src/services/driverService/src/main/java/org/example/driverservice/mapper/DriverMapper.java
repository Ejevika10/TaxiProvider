package org.example.driverservice.mapper;

import org.example.driverservice.dto.DriverRequestDTO;
import org.example.driverservice.dto.DriverResponseDTO;
import org.example.driverservice.model.Driver;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DriverMapper {
    Driver toDriver(DriverRequestDTO driverRequestDTO);

    Driver toDriver(DriverResponseDTO driverResponseDTO);

    DriverRequestDTO toDriverRequestDTO(Driver driver);

    DriverResponseDTO toDriverResponseDTO(Driver driver);
}
