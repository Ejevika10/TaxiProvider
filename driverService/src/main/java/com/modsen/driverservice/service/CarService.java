package com.modsen.driverservice.service;

import com.modsen.driverservice.dto.CarRequestDTO;
import com.modsen.driverservice.dto.CarResponseDTO;
import com.modsen.driverservice.dto.PageDTO;

import java.util.List;

public interface CarService {
    List<CarResponseDTO> getAllCars();

    PageDTO<CarResponseDTO> getPageCars(Integer limit, Integer offset);

    List<CarResponseDTO> getAllCarsByDriverId(Long driverId);

    CarResponseDTO getCarById(Long id);

    CarResponseDTO addCar(CarRequestDTO carRequestDTO);

    CarResponseDTO updateCar(CarRequestDTO carRequestDTO);

    void deleteCar(Long id);
}
