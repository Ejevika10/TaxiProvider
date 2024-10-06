package com.modsen.driverservice.service;

import com.modsen.driverservice.dto.CarRequestDTO;
import com.modsen.driverservice.dto.CarResponseDTO;
import com.modsen.driverservice.dto.PageDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public interface CarService {
    List<CarResponseDTO> getAllCars();

    PageDTO<CarResponseDTO> getPageCars(@Min(0) Integer offset, @Min(1) @Max(20) Integer limit);

    List<CarResponseDTO> getAllCarsByDriverId(@Min(0) Long driverId);

    CarResponseDTO getCarById(@Min(0) Long id);

    CarResponseDTO getCarByNumber(String number);

    CarResponseDTO addCar(@Valid CarRequestDTO carRequestDTO);

    CarResponseDTO updateCar(@Valid CarRequestDTO carRequestDTO);

    void deleteCar(@Min(0) Long id);
}
