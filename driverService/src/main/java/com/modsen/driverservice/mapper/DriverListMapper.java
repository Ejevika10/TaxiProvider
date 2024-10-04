package com.modsen.driverservice.mapper;

import com.modsen.driverservice.dto.DriverRequestDTO;
import com.modsen.driverservice.dto.DriverResponseDTO;
import com.modsen.driverservice.model.Driver;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = DriverMapper.class)
public interface DriverListMapper {
    List<Driver> toDriverList(List<DriverRequestDTO> driverRequestDTOList);

    List<DriverResponseDTO> toDriverResponseDTOList(List<Driver> driverList);
}