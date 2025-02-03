package com.modsen.driverservice.service;

import com.modsen.driverservice.dto.DriverCreateRequestDto;
import com.modsen.driverservice.dto.DriverRequestDto;
import com.modsen.driverservice.dto.DriverResponseDto;
import com.modsen.driverservice.dto.PageDto;
import com.modsen.driverservice.dto.UserRatingDto;

import java.util.List;
import java.util.UUID;

public interface DriverService {
    List<DriverResponseDto> getAllDrivers();

    PageDto<DriverResponseDto> getPageDrivers(Integer offset, Integer limit);

    DriverResponseDto getDriverById(UUID id);

    DriverResponseDto getDriverByEmail(String email);

    DriverResponseDto createDriver(DriverCreateRequestDto driverRequestDTO);

    DriverResponseDto updateDriver(UUID id, DriverRequestDto driverRequestDTO);

    DriverResponseDto updateRating(UserRatingDto userRatingDto);

    void deleteDriver(UUID id);
}
