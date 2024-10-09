package com.modsen.driverservice.service;

import com.modsen.driverservice.dto.DriverRequestDto;
import com.modsen.driverservice.dto.DriverResponseDto;
import com.modsen.driverservice.dto.PageDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public interface DriverService {
    List<DriverResponseDto> getAllDrivers();

    PageDto<DriverResponseDto> getPageDrivers(@Min(0) Integer offset, @Min(1) @Max(20) Integer limit);

    DriverResponseDto getDriverById(@Min(0) Long id);

    DriverResponseDto getDriverByEmail(String email);

    DriverResponseDto createDriver(@Valid DriverRequestDto driverRequestDTO);

    DriverResponseDto updateDriver(@Min(0) Long id, @Valid DriverRequestDto driverRequestDTO);

    void deleteDriver(@Min(0) Long id);
}
