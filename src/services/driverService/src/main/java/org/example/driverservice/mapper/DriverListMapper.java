package org.example.driverservice.mapper;

import org.example.driverservice.dto.DriverRequestDTO;
import org.example.driverservice.dto.DriverResponseDTO;
import org.example.driverservice.model.Driver;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = DriverMapper.class)
public interface DriverListMapper {
    List<Driver> toDriverList(List<DriverRequestDTO> driverRequestDTOList);

    List<DriverResponseDTO> toDriverResponseDTOList(List<Driver> driverList);
}