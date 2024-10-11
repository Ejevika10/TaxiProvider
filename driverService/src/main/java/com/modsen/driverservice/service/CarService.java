package com.modsen.driverservice.service;

import com.modsen.driverservice.dto.CarRequestDto;
import com.modsen.driverservice.dto.CarResponseDto;
import com.modsen.driverservice.dto.PageDto;

import java.util.List;

public interface CarService {
    List<CarResponseDto> getAllCars();

    PageDto<CarResponseDto> getPageCars(Integer offset, Integer limit);

    List<CarResponseDto> getAllCarsByDriverId(Long driverId);

    CarResponseDto getCarById(Long id);

    CarResponseDto getCarByNumber(String number);

    CarResponseDto addCar(CarRequestDto carRequestDTO);

    CarResponseDto updateCar(Long id, CarRequestDto carRequestDTO);

    void deleteCar(Long id);
}
