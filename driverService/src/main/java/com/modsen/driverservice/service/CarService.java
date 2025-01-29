package com.modsen.driverservice.service;

import com.modsen.driverservice.dto.CarRequestDto;
import com.modsen.driverservice.dto.CarResponseDto;
import com.modsen.driverservice.dto.PageDto;

import java.util.List;
import java.util.UUID;

public interface CarService {
    List<CarResponseDto> getAllCars();

    PageDto<CarResponseDto> getPageCars(Integer offset, Integer limit);

    List<CarResponseDto> getAllCarsByDriverId(UUID driverId);

    PageDto<CarResponseDto> getPageCarsByDriverId(UUID driverId, Integer offset, Integer limit);

    CarResponseDto getCarById(Long id);

    CarResponseDto getCarByNumber(String number);

    CarResponseDto addCar(CarRequestDto carRequestDTO);

    CarResponseDto updateCar(Long id, CarRequestDto carRequestDTO);

    void deleteCar(Long id);
}
