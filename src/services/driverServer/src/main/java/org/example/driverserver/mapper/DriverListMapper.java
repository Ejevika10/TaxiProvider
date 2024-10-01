package org.example.driverserver.mapper;

import org.example.driverserver.dto.DriverRequestDTO;
import org.example.driverserver.dto.DriverResponseDTO;
import org.example.driverserver.model.Driver;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = DriverMapper.class)
public interface DriverListMapper {
    List<Driver> toDriverList(List<DriverRequestDTO> driverRequestDTOList);

    List<DriverResponseDTO> toDriverResponseDTOList(List<Driver> driverList);
}