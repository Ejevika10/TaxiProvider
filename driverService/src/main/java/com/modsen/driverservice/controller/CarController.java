package com.modsen.driverservice.controller;

import com.modsen.driverservice.dto.CarRequestDto;
import com.modsen.driverservice.dto.CarResponseDto;
import com.modsen.driverservice.dto.PageDto;
import com.modsen.driverservice.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cars")
@RequiredArgsConstructor
public class CarController {
    private final CarService carService;

    @GetMapping
    public PageDto<CarResponseDto> getPageCars(@RequestParam (defaultValue = "0") Integer offset, @RequestParam (defaultValue = "5") Integer limit) {
        return carService.getPageCars(offset, limit);
    }

    @GetMapping("/{id}")
    public CarResponseDto getCar(@PathVariable Long id) {
        return carService.getCarById(id);
    }

    @GetMapping("/driver/{driverId}")
    public List<CarResponseDto> getCarsByDriverId(@PathVariable Long driverId) {
        return carService.getAllCarsByDriverId(driverId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CarResponseDto createCar(@RequestBody CarRequestDto carRequestDTO) {
        return carService.addCar(carRequestDTO);
    }

    @PutMapping("/{id}")
    public CarResponseDto updateCar(@PathVariable Long id, @RequestBody CarRequestDto carRequestDTO) {
        return carService.updateCar(id, carRequestDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
    }

}
