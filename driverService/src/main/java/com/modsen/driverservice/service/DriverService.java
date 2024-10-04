package com.modsen.driverservice.service;

import com.modsen.driverservice.dto.DriverRequestDTO;
import com.modsen.driverservice.dto.DriverResponseDTO;

import java.util.List;

public interface DriverService {
    List<DriverResponseDTO> getAllDrivers();

    DriverResponseDTO getDriverById(Long id);

    DriverResponseDTO createDriver(DriverRequestDTO driverRequestDTO);

    DriverResponseDTO updateDriver(DriverRequestDTO driverRequestDTO);

    void deleteDriver(Long id);
}
