package com.modsen.driverservice.service;

import com.modsen.driverservice.dto.DriverRequestDTO;
import com.modsen.driverservice.dto.DriverResponseDTO;
import com.modsen.driverservice.dto.PageDTO;

import java.util.List;

public interface DriverService {
    List<DriverResponseDTO> getAllDrivers();

    PageDTO<DriverResponseDTO> getPageDrivers(Integer offset, Integer limit);

    DriverResponseDTO getDriverById(Long id);

    DriverResponseDTO createDriver(DriverRequestDTO driverRequestDTO);

    DriverResponseDTO updateDriver(DriverRequestDTO driverRequestDTO);

    void deleteDriver(Long id);
}
