package org.example.driverserver.service;

import org.example.driverserver.dto.DriverRequestDTO;
import org.example.driverserver.dto.DriverResponseDTO;

import java.util.List;

public interface DriverService {
    List<DriverResponseDTO> getAllDrivers();

    DriverResponseDTO getDriverById(Long id);

    DriverResponseDTO createDriver(DriverRequestDTO driverRequestDTO);

    DriverResponseDTO updateDriver(DriverRequestDTO driverRequestDTO);

    void deleteDriver(Long id);
}
