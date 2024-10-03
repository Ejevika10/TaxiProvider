package org.example.driverservice.service;

import org.example.driverservice.dto.DriverRequestDTO;
import org.example.driverservice.dto.DriverResponseDTO;

import java.util.List;

public interface DriverService {
    List<DriverResponseDTO> getAllDrivers();

    DriverResponseDTO getDriverById(Long id);

    DriverResponseDTO createDriver(DriverRequestDTO driverRequestDTO);

    DriverResponseDTO updateDriver(DriverRequestDTO driverRequestDTO);

    void deleteDriver(Long id);
}
