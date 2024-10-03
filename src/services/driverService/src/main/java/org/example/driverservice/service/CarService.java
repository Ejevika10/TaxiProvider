package org.example.driverservice.service;

import org.example.driverservice.dto.CarRequestDTO;
import org.example.driverservice.dto.CarResponseDTO;

import java.util.List;

public interface CarService {
    List<CarResponseDTO> getAllCars();

    List<CarResponseDTO> getAllCarsByDriverId(Long driverId);

    CarResponseDTO getCarById(Long id);

    CarResponseDTO addCar(CarRequestDTO carRequestDTO);

    CarResponseDTO updateCar(CarRequestDTO carRequestDTO);

    void deleteCar(Long id);
}
