package com.modsen.driverservice.service;

import com.modsen.driverservice.dto.CarRequestDTO;
import com.modsen.driverservice.dto.CarResponseDTO;

import java.util.List;

public interface CarService {
    List<CarResponseDTO> getAllCars();

    List<CarResponseDTO> getAllCarsByDriverId(Long driverId);

    CarResponseDTO getCarById(Long id);

    CarResponseDTO addCar(CarRequestDTO carRequestDTO);

    CarResponseDTO updateCar(CarRequestDTO carRequestDTO);

    void deleteCar(Long id);
}
