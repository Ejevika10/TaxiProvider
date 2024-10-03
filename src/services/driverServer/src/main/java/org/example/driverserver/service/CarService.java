package org.example.driverserver.service;

import org.example.driverserver.dto.CarRequestDTO;
import org.example.driverserver.dto.CarResponseDTO;

import java.util.List;

public interface CarService {
    List<CarResponseDTO> getAllCars();

    List<CarResponseDTO> getAllCarsByDriverId(Long driverId);

    CarResponseDTO getCarById(Long id);

    CarResponseDTO addCar(CarRequestDTO carRequestDTO);

    CarResponseDTO updateCar(CarRequestDTO carRequestDTO);

    void deleteCar(Long id);
}
