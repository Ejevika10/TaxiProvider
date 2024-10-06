package com.modsen.driverservice.service;

import com.modsen.driverservice.dto.DriverRequestDTO;
import com.modsen.driverservice.dto.DriverResponseDTO;
import com.modsen.driverservice.dto.PageDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public interface DriverService {
    List<DriverResponseDTO> getAllDrivers();

    PageDTO<DriverResponseDTO> getPageDrivers(@Min(0) Integer offset, @Min(1) @Max(20) Integer limit);

    DriverResponseDTO getDriverById(@Min(0) Long id);

    DriverResponseDTO getDriverByEmail(String email);

    DriverResponseDTO createDriver(@Valid DriverRequestDTO driverRequestDTO);

    DriverResponseDTO updateDriver(@Valid DriverRequestDTO driverRequestDTO);

    void deleteDriver(@Min(0) Long id);
}
