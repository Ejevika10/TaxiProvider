package com.modsen.driverservice.service;

import com.modsen.driverservice.dto.CarRequestDto;
import com.modsen.driverservice.dto.CarResponseDto;
import com.modsen.driverservice.dto.PageDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public interface CarService {
    List<CarResponseDto> getAllCars();

    PageDto<CarResponseDto> getPageCars(@Min(0) Integer offset, @Min(1) @Max(20) Integer limit);

    List<CarResponseDto> getAllCarsByDriverId(@Min(0) Long driverId);

    CarResponseDto getCarById(@Min(0) Long id);

    CarResponseDto getCarByNumber(String number);

    CarResponseDto addCar(@Valid CarRequestDto carRequestDTO);

    CarResponseDto updateCar(@Min(0) Long id, @Valid CarRequestDto carRequestDTO);

    void deleteCar(@Min(0) Long id);
}
