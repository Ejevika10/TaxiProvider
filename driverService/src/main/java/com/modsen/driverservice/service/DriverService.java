package com.modsen.driverservice.service;

import com.modsen.driverservice.dto.DriverRequestDto;
import com.modsen.driverservice.dto.DriverResponseDto;
import com.modsen.driverservice.dto.PageDto;

import java.util.List;

public interface DriverService {
    List<DriverResponseDto> getAllDrivers();

    PageDto<DriverResponseDto> getPageDrivers(Integer offset, Integer limit);

    DriverResponseDto getDriverById(Long id);

    DriverResponseDto getDriverByEmail(String email);

    DriverResponseDto createDriver(DriverRequestDto driverRequestDTO);

    DriverResponseDto updateDriver(Long id, DriverRequestDto driverRequestDTO);

    void deleteDriver(Long id);
}
